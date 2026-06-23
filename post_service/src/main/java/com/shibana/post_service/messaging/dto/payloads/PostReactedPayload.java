package com.shibana.post_service.messaging.dto.payloads;

import com.shibana.post_service.model.enums.ReactionActionEnum;
import com.shibana.post_service.model.enums.ReactionTargetTypeEnum;
import com.shibana.post_service.model.enums.ReactionTypeEnum;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostReactedPayload {
    UUID requesterId;
    UUID targetId;
    ReactionTargetTypeEnum reactionTargetType;
    ReactionTypeEnum reactionType;
    ReactionActionEnum reactionAction;
}
