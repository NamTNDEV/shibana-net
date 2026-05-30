package com.shibana.post_service.model.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorResponse {
    UUID id;
    String username;
    String firstName;
    String lastName;
    String avatarUrl;
    Double avatarScale;
    Double avatarPositionX;
    Double avatarPositionY;
}
