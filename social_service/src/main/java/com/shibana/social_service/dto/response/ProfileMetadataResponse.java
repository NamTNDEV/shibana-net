package com.shibana.social_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileMetadataResponse {
    String firstName;
    String lastName;
    String avatar;
    Double avatarScale;
    Double avatarPositionX;
    Double avatarPositionY;
}
