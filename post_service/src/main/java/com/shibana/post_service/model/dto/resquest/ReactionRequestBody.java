package com.shibana.post_service.model.dto.resquest;

import com.shibana.post_service.model.enums.ReactionTypeEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactionRequestBody {
    ReactionTypeEnum reactionType;
}
