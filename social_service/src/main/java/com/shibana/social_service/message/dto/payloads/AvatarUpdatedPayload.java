package com.shibana.social_service.message.dto.payloads;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvatarUpdatedPayload {
    UUID userId;
    String avatarMediaName;
    Double avatarScale;
    Double avatarPositionX;
    Double avatarPositionY;

}