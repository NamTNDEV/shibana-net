package com.shibana.post_service.service.cache;

import com.shibana.post_service.model.entity.Reaction;
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
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionCacheService {
    ReactionRepo reactionRepo;
    RedisHashHelper redisHashHelper;
    RedisCacheTemplate redisCacheTemplate;

    String EMPTY_KEY_REDIS_FLAG = "_EMPTY_FLAG_";
    Duration TTL_HAS_DATA = Duration.ofDays(7);
    Duration TTL_EMPTY = Duration.ofMinutes(10);

    private void handleRemoveStatsField(Long count, String statsCacheKey, String field) {
        if (count != null && count <= 0) {
            redisHashHelper.hDelete(statsCacheKey, field);
        }
    }

    public boolean toggleReaction(UUID targetUUID, UUID requesterUUID, ReactionTargetTypeEnum targetType, ReactionTypeEnum reactionType) {
        String reactionCacheKey = String.format("p:%s:%s:react", targetType.name(), targetUUID);
        String statsCacheKey = String.format("p:%s:%s:stats", targetType.name(), targetUUID);
        String userIdStr = requesterUUID.toString();
        long WARMUP_LOCK_TIMEOUT_MS = 500L;
        String TOTAL_STATS_REDIS_KEY = "TOTAL";

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

        String currentReaction = (String) redisHashHelper.hGet(reactionCacheKey, userIdStr);
        if (currentReaction == null) {
            // Trường hợp 1: Chưa có Reaction
            redisHashHelper.hSet(reactionCacheKey, userIdStr, reactionType.name(), TTL_HAS_DATA);
            redisHashHelper.hDelete(reactionCacheKey, EMPTY_KEY_REDIS_FLAG);

            redisHashHelper.hIncrBy(statsCacheKey, reactionType.name(), 1);
            redisHashHelper.hIncrBy(statsCacheKey, TOTAL_STATS_REDIS_KEY, 1);
            redisHashHelper.hDelete(statsCacheKey, EMPTY_KEY_REDIS_FLAG);
            return true;
        }

        // Trường hợp 2: Đã có Reaction và giống với Reaction hiện tại => Xóa Reaction
        if (currentReaction.equals(reactionType.name())) {
            redisHashHelper.hDelete(reactionCacheKey, userIdStr);

            Long newReactionCount = redisHashHelper.hIncrBy(statsCacheKey, reactionType.name(), -1);
            Long newTotalCount = redisHashHelper.hIncrBy(statsCacheKey, TOTAL_STATS_REDIS_KEY, -1);

            handleRemoveStatsField(newReactionCount, statsCacheKey, reactionType.name());
            handleRemoveStatsField(newTotalCount, statsCacheKey, TOTAL_STATS_REDIS_KEY);
            return false;
        }

        // Trường hợp 3: Đã có Reaction nhưng khác với Reaction hiện tại => Cập nhật Reaction
        redisHashHelper.hSet(reactionCacheKey, userIdStr, reactionType.name(), TTL_HAS_DATA);

        Long newOldReactionCount = redisHashHelper.hIncrBy(statsCacheKey, currentReaction, -1);
        redisHashHelper.hIncrBy(statsCacheKey, reactionType.name(), 1);

        handleRemoveStatsField(newOldReactionCount, statsCacheKey, currentReaction);
        return true;
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
        log.info("[ReactionCacheService::fetchReactionStatsFromDb] Bắt đầu múc dữ liệu Stats cho targetId: {}", targetUUID);

        // 1. Gọi Database thực hiện tính toán (Count & Group By)
        List<ReactionCountProjection> statsProjections = reactionRepo.countReactionsByTargetId(targetUUID);

        // 2. Tấm khiên chống Xuyên thấu Cache (Penetration)
        if (statsProjections == null || statsProjections.isEmpty()) {
            log.info("[ReactionCacheService::fetchReactionStatsFromDb] TargetId: {} chưa có lượt tương tác nào. Đặt cờ EMPTY.", targetUUID);
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
        String TOTAL_STATS_REDIS_KEY = "TOTAL"; // Có thể đưa biến này lên làm hằng số chung của class
        statsMap.put(TOTAL_STATS_REDIS_KEY, String.valueOf(totalReactions));

        // 6. Đẩy toàn bộ cấu trúc Hash lên Redis trong 1 hit duy nhất (O(1) Network I/O)
        log.info("[ReactionCacheService::fetchReactionStatsFromDb] Đẩy dữ liệu Stats lên Redis cho targetId: {}. Dữ liệu: {}", targetUUID, statsMap);
        redisHashHelper.hPutAll(statsCacheKey, statsMap, TTL_HAS_DATA);

        log.info("[ReactionCacheService::fetchReactionStatsFromDb] Warm-up thành công! TargetId: {} có tổng cộng {} lượt tương tác.", targetUUID, totalReactions);
    }
}
