package com.shibana.social_service.messaging.dto.payloads;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvatarUpdatedPayload {
    String userId;
    String avatarMediaName;
    Double avatarScale;
    Double avatarPositionX;
    Double avatarPositionY;

}