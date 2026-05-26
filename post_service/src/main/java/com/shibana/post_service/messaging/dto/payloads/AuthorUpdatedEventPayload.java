package com.shibana.post_service.messaging.dto.payloads;

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
public class AuthorUpdatedEventPayload {
    UUID userId;
    String avatarMediaName;
    Double avatarScale;
    Double avatarPositionX;
    Double avatarPositionY;
}
