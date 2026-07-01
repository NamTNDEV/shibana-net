package com.shibana.post_service.strategy;

import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.model.enums.ReactionTargetTypeEnum;
import com.shibana.post_service.model.enums.ReactionTypeEnum;
import com.shibana.post_service.service.PostCommandService;
import com.shibana.post_service.service.PostQueryService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class PostReactionStrategy implements ReactionStrategy {
    PostQueryService  postQueryService;
    PostCommandService postCommandService;

    @Override
    public ReactionTargetTypeEnum getTargetType() {
        return ReactionTargetTypeEnum.POST;
    }

    @Override
    public void updateStats(UUID targetUUID, int amount) {
        postCommandService.updatePostReactionStats(targetUUID, amount);
    }

    @Override
    public void updateBatchStats(Set<UUID> targetIds) {
        postCommandService.updatePostBatchReactionStats(targetIds);
    }

    @Override
    public void validateTarget(UUID id) {
        if (!postQueryService.checkPostExist(id)) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }
    }
}
