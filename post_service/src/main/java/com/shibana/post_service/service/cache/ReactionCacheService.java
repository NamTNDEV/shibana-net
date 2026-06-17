package com.shibana.post_service.service.cache;

import com.shibana.post_service.model.entity.Reaction;
import com.shibana.post_service.model.enums.ReactionTargetTypeEnum;
import com.shibana.post_service.model.enums.ReactionTypeEnum;
import com.shibana.post_service.repo.ReactionRepo;
import com.shibana.post_service.service.ReactionService;
import com.shibana.post_service.service.cache.base.RedisHashHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    /**
     * Xử lý logic Toggle Reaction trên cấu trúc Redis Hash.
     *
     * @return true nếu ghi nhận cảm xúc mới/đổi cảm xúc, false nếu hủy cảm xúc.
     */
    public boolean toggleReaction(UUID targetUUID, UUID requesterUUID, ReactionTargetTypeEnum targetType, ReactionTypeEnum reactionType) {
        // 1. Quản lý định dạng Key nội bộ trong Cache Layer
        String redisKey = String.format("p:%s:%s:react", targetType.name(), targetUUID);
        String userIdStr = requesterUUID.toString();

        // 2. Chốt chặn Hồi sinh Cache (Đảm bảo RAM luôn chứa đủ dữ liệu trước khi xử lý)
        warmUpCacheIfNeeded(redisKey, targetUUID);

        // 3. Xử lý logic Toggle trên Hash
        // Lấy loại Reaction hiện tại của User (nếu có)
        String currentReaction = (String) redisHashHelper.hGet(redisKey, userIdStr);

        if (currentReaction != null && currentReaction.equals(reactionType.name())) {
            // Trường hợp 1: Đã thả LIKE, giờ bấm LIKE lần nữa -> Trở thành UNLIKE (Hủy)
            redisHashHelper.hDelete(redisKey, userIdStr);
            return false;

        } else {
            // Trường hợp 2: Chưa thả gì -> Cập nhật thành LIKE
            // Trường hợp 3: Đã thả LIKE, giờ bấm LOVE -> Ghi đè LOVE lên (Cực mượt, không cần phải xóa trước)
            // Gia hạn Sliding Expiration (7 ngày) vì bài Post vừa có tương tác
            redisHashHelper.hSet(redisKey, userIdStr, reactionType.name(), Duration.ofDays(7));
            return true;
        }
    }

    /**
     * Logic múc dữ liệu từ DB lên RAM nếu RAM bị trống (Cold Cache)
     */
    void warmUpCacheIfNeeded(String redisKey, UUID targetUUID) {
        if (!redisHashHelper.hasKey(redisKey)) {
            log.info("Cache miss cho key {}. Tiến hành Warm-up từ Postgres...", redisKey);

            // Lấy toàn bộ lịch sử reaction của target này từ DB
            List<Reaction> historicalReactions = reactionRepo.findAllByTargetId(targetUUID).orElse(List.of());

            if (!historicalReactions.isEmpty()) {
                // Dùng Map để putAll vào Redis Hash trong 1 lần gọi mạng duy nhất (Tối ưu Pipeline/Batch)
                Map<String, Object> reactionMap = historicalReactions.stream()
                        .collect(Collectors.toMap(
                                r -> r.getAuthorId().toString(),
                                r -> r.getReactionType().name()
                        ));

                redisHashHelper.hPutAll(redisKey, reactionMap, Duration.ofDays(7));
            }
        }
    }
}
