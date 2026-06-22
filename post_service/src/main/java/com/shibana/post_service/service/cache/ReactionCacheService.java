package com.shibana.post_service.service.cache;

import com.shibana.post_service.model.entity.Reaction;
import com.shibana.post_service.model.enums.ReactionTargetTypeEnum;
import com.shibana.post_service.model.enums.ReactionTypeEnum;
import com.shibana.post_service.repo.ReactionRepo;
import com.shibana.post_service.service.cache.base.RedisHashHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.Instant;
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

    static String EMPTY_KEY_REDIS_FLAG = "_EMPTY_FLAG_";
    static Duration TTL_HAS_DATA = Duration.ofDays(7);
    static Duration TTL_EMPTY = Duration.ofHours(2);

    /**
     * Xử lý logic Toggle Reaction trên cấu trúc Redis Hash.
     *
     * @return true nếu ghi nhận cảm xúc mới/đổi cảm xúc, false nếu hủy cảm xúc.
     */
    public boolean toggleReaction(UUID targetUUID, UUID requesterUUID, ReactionTargetTypeEnum targetType, ReactionTypeEnum reactionType) {
        String redisKey = String.format("p:%s:%s:react", targetType.name(), targetUUID);
        String userIdStr = requesterUUID.toString();

        warmUpCacheIfNeeded(redisKey, targetUUID);

        String currentReaction = (String) redisHashHelper.hGet(redisKey, userIdStr);
        if (currentReaction != null && currentReaction.equals(reactionType.name())) {
            redisHashHelper.hDelete(redisKey, userIdStr);
            return false;
        } else {
            redisHashHelper.hSet(redisKey, userIdStr, reactionType.name(), TTL_HAS_DATA);
            redisHashHelper.hDelete(redisKey, EMPTY_KEY_REDIS_FLAG);
            return true;
        }
    }

    /**
     * Logic múc dữ liệu từ DB lên RAM nếu RAM bị trống (Cold Cache)
     */
    void warmUpCacheIfNeeded(String redisKey, UUID targetUUID) {
        if (redisHashHelper.hasKey(redisKey)) return;
        log.info("[warmUpCacheIfNeeded]::Cache miss cho key {}. Tiến hành Warm-up từ Postgres...", redisKey);

        doWarmUpFromDb(redisKey, targetUUID);
    }

    void doWarmUpFromDb(String redisKey, UUID targetUUID) {
        List<Reaction> historicalReactions = reactionRepo.findAllByTargetId(targetUUID).orElse(List.of());
        if (!historicalReactions.isEmpty()) {
            log.info("[doWarmUpFromDb]::Warm-up thành công cho key {} với {} reactions.", redisKey, historicalReactions.size());
            Map<String, Object> reactionMap = historicalReactions.stream()
                    .collect(Collectors.toMap(
                            r -> r.getAuthorId().toString(),
                            r -> r.getReactionType().name()
                    ));
            redisHashHelper.hPutAll(redisKey, reactionMap, TTL_HAS_DATA);
        } else {
            log.info("[doWarmUpFromDb]::Không có dữ liệu lịch sử cho key {}. Không thực hiện Warm-up.", redisKey);
            redisHashHelper.hSet(redisKey, EMPTY_KEY_REDIS_FLAG, "TRUE", TTL_EMPTY);
        }
    }
}
