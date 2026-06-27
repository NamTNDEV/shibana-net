package com.shibana.post_service.service;

import com.shibana.post_service.messaging.dto.EventType;
import com.shibana.post_service.messaging.dto.payloads.ReactedPayload;
import com.shibana.post_service.messaging.publisher.impl.KafkaEventPublisher;
import com.shibana.post_service.model.entity.Reaction;
import com.shibana.post_service.model.enums.AggregateTypeEnum;
import com.shibana.post_service.model.enums.ReactionActionEnum;
import com.shibana.post_service.model.enums.ReactionTargetTypeEnum;
import com.shibana.post_service.model.enums.ReactionTypeEnum;
import com.shibana.post_service.repo.ReactionRepo;
import com.shibana.post_service.repo.jdbc_repo.ReactionJdbcRepository;
import com.shibana.post_service.service.cache.ReactionCacheService;
import com.shibana.post_service.strategy.ReactionStrategy;
import com.shibana.post_service.strategy.ReactionStrategyFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionService {
    ReactionRepo reactionRepo;
    ReactionStrategyFactory strategyFactory;
    KafkaEventPublisher kafkaEventPublisher;
    ReactionCacheService reactionCacheService;
    ReactionJdbcRepository reactionJdbcRepository;

    @Transactional
    public void handleReactionV1(UUID requesterUUID, UUID targetUUID, ReactionTargetTypeEnum reactionTargetTypeEnum, ReactionTypeEnum reactionTypeEnum) {
        ReactionStrategy strategy = strategyFactory.getStrategy(reactionTargetTypeEnum);
        strategy.validateTarget(targetUUID);

        Optional<Reaction> existingReaction = reactionRepo.findByTargetIdAndAuthorId(targetUUID, requesterUUID);
        if (existingReaction.isPresent()) {
            reactionRepo.delete(existingReaction.get());
            strategy.updateStats(requesterUUID, targetUUID, -1);
        } else {
            Reaction reactionEntity = Reaction.builder()
                    .targetId(targetUUID)
                    .authorId(requesterUUID)
                    .reactionType(reactionTypeEnum)
                    .targetType(reactionTargetTypeEnum)
                    .build();
            reactionRepo.save(reactionEntity);
            strategy.updateStats(requesterUUID, targetUUID, 1);
        }
    }

    /**
     * V2 - using Redis for handling high-concurrency requesting
     */
    public String handleReactionV2(UUID requesterUUID, UUID targetUUID, ReactionTargetTypeEnum reactionTargetTypeEnum, ReactionTypeEnum reactionTypeEnum) {
        ReactionStrategy strategy = strategyFactory.getStrategy(reactionTargetTypeEnum);
        strategy.validateTarget(targetUUID);

        boolean isAddedOrUpdated = reactionCacheService.toggleReaction(targetUUID, requesterUUID, reactionTargetTypeEnum, reactionTypeEnum);

        ReactedPayload eventPayload = ReactedPayload.builder()
                .requesterId(requesterUUID)
                .targetId(targetUUID)
                .reactionType(reactionTypeEnum)
                .reactionTargetType(reactionTargetTypeEnum)
                .build();

        String result = isAddedOrUpdated ? "React successfully" : "Unreact successfully";
        if (!isAddedOrUpdated) {
            eventPayload.setReactionAction(ReactionActionEnum.DELETE);
        } else {
            eventPayload.setReactionAction(ReactionActionEnum.CREATE);
        }

        kafkaEventPublisher.publishEvent(
                eventPayload,
                AggregateTypeEnum.REACTION,
                targetUUID.toString(),
                EventType.USER_REACTED
        );

        return result;
    }

    @Transactional
    public void batchUpsertToDb(List<ReactedPayload> payloads) {
        if (payloads.isEmpty()) {
            return;
        }

        Map<Boolean, List<ReactedPayload>> groupedPayloads = payloads.stream()
                .collect(Collectors.partitioningBy(payload -> payload.getReactionAction() == ReactionActionEnum.CREATE));

        List<ReactedPayload> upsertPayloads = groupedPayloads.get(true);
        List<ReactedPayload> deletePayloads = groupedPayloads.get(false);

        if (!upsertPayloads.isEmpty()) {
            reactionJdbcRepository.batchUpsert(upsertPayloads);
        }
        if (!deletePayloads.isEmpty()) {
            reactionJdbcRepository.batchDelete(deletePayloads);
        }
    }

    public List<ReactedPayload> generateFakePayloads(int n) {
        List<ReactedPayload> payloads = new ArrayList<>(n);
        Random random = new Random();

        // 1. Giả lập một danh sách "Hot Posts" (Khoảng 5 bài viết đang nổi đình nổi đám)
        // Để cố tình ép hệ thống tạo ra các cú "ON CONFLICT" và Update đè dữ liệu
        List<UUID> hotPostIds = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            hotPostIds.add(UUID.randomUUID());
        }

        // 2. Lấy danh sách các Enum hiện có
        ReactionTypeEnum[] reactionTypes = ReactionTypeEnum.values();
        // Tỉ lệ nghiêng về thả Like (CREATE) nhiều hơn là Rút Like (DELETE) để giống thực tế
        ReactionActionEnum[] actions = {
                ReactionActionEnum.CREATE, ReactionActionEnum.CREATE, ReactionActionEnum.CREATE,
                ReactionActionEnum.DELETE
        };

        for (int i = 0; i < n; i++) {
            // 80% traffic dồn vào 5 bài Hot Posts, 20% rơi vào các bài viết vãng lai
            UUID targetId = random.nextInt(100) < 80
                    ? hotPostIds.get(random.nextInt(hotPostIds.size()))
                    : UUID.randomUUID();

            // Mỗi event là một User khác nhau thả cảm xúc
            UUID requesterId = UUID.randomUUID();

            ReactedPayload payload = ReactedPayload.builder()
                    .targetId(targetId)
                    .requesterId(requesterId)
                    .reactionType(reactionTypes[random.nextInt(reactionTypes.length)])
                    .reactionAction(actions[random.nextInt(actions.length)])
                    .reactionTargetType(ReactionTargetTypeEnum.POST)
                    .build();

            payloads.add(payload);
        }
        return payloads;
    }
}
