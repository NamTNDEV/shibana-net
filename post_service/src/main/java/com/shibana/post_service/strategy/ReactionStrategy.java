package com.shibana.post_service.strategy;

import com.shibana.post_service.model.enums.ReactionTargetTypeEnum;
import com.shibana.post_service.model.enums.ReactionTypeEnum;

import java.util.UUID;

public interface ReactionStrategy {
    ReactionTargetTypeEnum getTargetType();
    void updateStats(UUID requesterUUID, UUID targetUUID, int amount, ReactionTypeEnum reactionTypeEnum);
    void validateTarget(UUID id);
}
