package com.shibana.post_service.messaging.dto.payloads;

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

    public boolean isValid() {
        return userId != null && avatarMediaName != null && avatarScale != null
                && avatarPositionX != null && avatarPositionY != null;
    }
}
