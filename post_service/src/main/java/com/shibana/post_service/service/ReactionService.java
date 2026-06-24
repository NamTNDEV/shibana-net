package com.shibana.post_service.service;

import com.shibana.post_service.messaging.dto.EventType;
import com.shibana.post_service.messaging.dto.payloads.PostReactedPayload;
import com.shibana.post_service.messaging.publisher.impl.KafkaEventPublisher;
import com.shibana.post_service.model.entity.Reaction;
import com.shibana.post_service.model.enums.AggregateTypeEnum;
import com.shibana.post_service.model.enums.ReactionActionEnum;
import com.shibana.post_service.model.enums.ReactionTargetTypeEnum;
import com.shibana.post_service.model.enums.ReactionTypeEnum;
import com.shibana.post_service.repo.ReactionRepo;
import com.shibana.post_service.service.cache.ReactionCacheService;
import com.shibana.post_service.strategy.ReactionStrategy;
import com.shibana.post_service.strategy.ReactionStrategyFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionService {
    ReactionRepo reactionRepo;
    ReactionStrategyFactory strategyFactory;
    KafkaEventPublisher kafkaEventPublisher;
    ReactionCacheService reactionCacheService;

    @Transactional
    public void handleReactionV1(UUID requesterUUID, UUID targetUUID, ReactionTargetTypeEnum reactionTargetTypeEnum, ReactionTypeEnum reactionTypeEnum) {
        ReactionStrategy strategy = strategyFactory.getStrategy(reactionTargetTypeEnum);
        strategy.validateTarget(targetUUID);

        Optional<Reaction> existingReaction = reactionRepo.findByTargetIdAndAuthorId(targetUUID, requesterUUID);
        if (existingReaction.isPresent()) {
            log.info("Reaction already exists for target UUID {}", targetUUID);
            reactionRepo.delete(existingReaction.get());
            strategy.updateStats(requesterUUID, targetUUID, -1, reactionTypeEnum);
        } else {
            log.info("Creating reaction for target UUID {}", targetUUID);
            Reaction reactionEntity = Reaction.builder()
                    .targetId(targetUUID)
                    .authorId(requesterUUID)
                    .reactionType(reactionTypeEnum)
                    .targetType(reactionTargetTypeEnum)
                    .build();
            reactionRepo.save(reactionEntity);
            strategy.updateStats(requesterUUID, targetUUID, 1, reactionTypeEnum);
        }
    }

    /**
     * V2 - using Redis for handling high-concurrency requesting
     */
    public void handleReactionV2(UUID requesterUUID, UUID targetUUID, ReactionTargetTypeEnum reactionTargetTypeEnum, ReactionTypeEnum reactionTypeEnum) {
        ReactionStrategy strategy = strategyFactory.getStrategy(reactionTargetTypeEnum);
        strategy.validateTarget(targetUUID);

//        boolean isAddedOrUpdated = reactionCacheService.toggleReaction(targetUUID, requesterUUID, reactionTargetTypeEnum, reactionTypeEnum);
        boolean isAddedOrUpdated = true;

        PostReactedPayload eventPayload = PostReactedPayload.builder()
                .requesterId(requesterUUID)
                .targetId(targetUUID)
                .reactionType(reactionTypeEnum)
                .reactionTargetType(reactionTargetTypeEnum)
                .build();

        if (!isAddedOrUpdated) {
            log.info("User {} tiến hành UNLIKE/REMOVE target UUID {}", requesterUUID, targetUUID);
            eventPayload.setReactionAction(ReactionActionEnum.DELETE);

        } else {
            log.info("User {} tiến hành {} target UUID {}", requesterUUID, reactionTypeEnum.name(), targetUUID);
            eventPayload.setReactionAction(ReactionActionEnum.CREATE);
        }

        kafkaEventPublisher.publishEvent(
                eventPayload,
                AggregateTypeEnum.REACTION,
                targetUUID.toString(),
                EventType.POST_REACTED
        );
    }
}
