package com.shibana.identity_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyAccountResponse {
    String userId;
    String email;
    String username;
    Set<RoleResponse> roles;
    String firstName;
    String lastName;
    String avatar;
    Double avatarScale;
    Double avatarPositionX;
    Double avatarPositionY;
}
