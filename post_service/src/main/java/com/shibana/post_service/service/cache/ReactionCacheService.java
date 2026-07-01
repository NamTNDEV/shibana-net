package com.shibana.post_service.service.cache;

import com.shibana.post_service.model.entity.Reaction;
import com.shibana.post_service.model.enums.ReactionActionEnum;
import com.shibana.post_service.model.enums.ReactionTargetTypeEnum;
import com.shibana.post_service.model.enums.ReactionTypeEnum;
import com.shibana.post_service.repo.ReactionRepo;
import com.shibana.post_service.repo.projection.ReactionCountProjection;
import com.shibana.post_service.service.cache.base.RedisCacheTemplate;
import com.shibana.post_service.service.cache.base.RedisHashHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionCacheService {
    ReactionRepo reactionRepo;
    RedisHashHelper redisHashHelper;
    RedisCacheTemplate redisCacheTemplate;
    StringRedisTemplate stringRedisTemplate;
    RedisScript<String> toggleReactionScript;

    public static final String TOTAL_STATS_REDIS_KEY = "TOTAL";
    public static final String EMPTY_KEY_REDIS_FLAG = "_EMPTY_FLAG_";
    public static final String CACHE_KEY_PREFIX = "p:%s:%s";
    public static final String STATS_CACHE_KEY = CACHE_KEY_PREFIX + ":stats";
    public static final String REACTION_CACHE_KEY = CACHE_KEY_PREFIX + ":reactions";

    public static final Duration TTL_HAS_DATA = Duration.ofDays(7);
    public static final Duration TTL_EMPTY = Duration.ofMinutes(10);

    public Map<UUID, Map<String, Object>> getBatchReactionStats(List<UUID> targetUUIDs, ReactionTargetTypeEnum targetType) {
        if (targetUUIDs == null || targetUUIDs.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> keys = targetUUIDs.stream()
                .map(uuid -> String.format(STATS_CACHE_KEY, targetType.name(), uuid))
                .toList();
        List<Map<Object, Object>> rawResults = redisHashHelper.hGetAllPipeline(keys);
        Map<UUID, Map<String, Object>> finalResult = new HashMap<>();
        for (int i = 0; i < rawResults.size(); i++) {
            Map<Object, Object> rawResult = rawResults.get(i);

            if (rawResult != null && !rawResult.isEmpty() && !rawResult.containsKey(EMPTY_KEY_REDIS_FLAG)) {
                Map<String, Object> cleanMap = rawResult.entrySet().stream()
                        .collect(Collectors.toMap(
                                e ->String.valueOf(e.getKey()),
                                Map.Entry::getValue
                        ));
                finalResult.put(targetUUIDs.get(i), cleanMap);
            }
        }

        return finalResult;
    }

    public ReactionActionEnum toggleReaction(UUID targetUUID, UUID requesterUUID, ReactionTargetTypeEnum targetType, ReactionTypeEnum reactionType) {
        String reactionCacheKey = String.format(REACTION_CACHE_KEY, targetType.name(), targetUUID);
        String statsCacheKey = String.format(STATS_CACHE_KEY, targetType.name(), targetUUID);
        String userIdStr = requesterUUID.toString();
        long WARMUP_LOCK_TIMEOUT_MS = 500L;

        redisCacheTemplate.executeWithDoubleCheckLock(
                reactionCacheKey,
                () -> fetchReactionDetailsFromDb(reactionCacheKey, targetUUID),
                WARMUP_LOCK_TIMEOUT_MS
        );

        redisCacheTemplate.executeWithDoubleCheckLock(
                statsCacheKey,
                () -> fetchReactionStatsFromDb(statsCacheKey, targetUUID),
                WARMUP_LOCK_TIMEOUT_MS
        );

        String actionStr = stringRedisTemplate.execute(
                toggleReactionScript,
                List.of(reactionCacheKey, statsCacheKey),
                userIdStr,
                reactionType.name(),
                EMPTY_KEY_REDIS_FLAG,
                String.valueOf(TTL_HAS_DATA.toSeconds())
        );

        return ReactionActionEnum.valueOf(actionStr);
    }

    void fetchReactionDetailsFromDb(String redisKey, UUID targetUUID) {
        log.info("[fetchReactionDetailsFromDb]::Warm-up cache for key: {}", redisKey);
        List<Reaction> historicalReactions = reactionRepo.findAllByTargetId(targetUUID).orElse(List.of());
        if (!historicalReactions.isEmpty()) {
            Map<String, Object> reactionMap = historicalReactions.stream()
                    .collect(Collectors.toMap(
                            r -> r.getAuthorId().toString(),
                            r -> r.getReactionType().name()
                    ));
            redisHashHelper.hPutAll(redisKey, reactionMap, TTL_HAS_DATA);
        } else {
            redisHashHelper.hSet(redisKey, EMPTY_KEY_REDIS_FLAG, "TRUE", TTL_EMPTY);
        }
    }

    void fetchReactionStatsFromDb(String statsCacheKey, UUID targetUUID) {
        // 1. Gọi Database thực hiện tính toán (Count & Group By)
        List<ReactionCountProjection> statsProjections = reactionRepo.countReactionsByTargetId(targetUUID);

        // 2. Tấm khiên chống Xuyên thấu Cache (Penetration)
        if (statsProjections == null || statsProjections.isEmpty()) {
            redisHashHelper.hSet(statsCacheKey, EMPTY_KEY_REDIS_FLAG, "TRUE", TTL_EMPTY);
            return;
        }

        // 3. Khởi tạo bộ lưu trữ tạm trên RAM để chuẩn bị đẩy lên Redis
        Map<String, Object> statsMap = new HashMap<>();
        long totalReactions = 0L;

        // 4. Nhào nặn dữ liệu: Duyệt qua 5-6 dòng kết quả từ DB
        for (ReactionCountProjection projection : statsProjections) {
            String typeStr = projection.getReactionType().name();
            Long count = projection.getCount();

            // Đưa từng loại (LIKE, LOVE...) vào Map
            statsMap.put(typeStr, String.valueOf(count));

            // Tích lũy tổng số
            totalReactions += count;
        }

        // 5. Gắn con số TỔNG (TOTAL) vào Map
        statsMap.put(TOTAL_STATS_REDIS_KEY, String.valueOf(totalReactions));

        // 6. Đẩy toàn bộ cấu trúc Hash lên Redis trong 1 hit duy nhất (O(1) Network I/O)
        redisHashHelper.hPutAll(statsCacheKey, statsMap, TTL_HAS_DATA);
    }
}
