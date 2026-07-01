package com.shibana.post_service.strategy;

import com.shibana.post_service.model.enums.ReactionTargetTypeEnum;
import com.shibana.post_service.model.enums.ReactionTypeEnum;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ReactionStrategy {
    ReactionTargetTypeEnum getTargetType();
    void updateStats(UUID targetUUID, int amount);
    void updateBatchStats(Set<UUID> targetIds);
    void validateTarget(UUID id);
}
