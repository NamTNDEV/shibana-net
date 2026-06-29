package com.shibana.post_service.repo.projection;

import com.shibana.post_service.model.enums.ReactionTypeEnum;

public interface ReactionCountProjection {
    ReactionTypeEnum getReactionType();
    Long getCount();
}
