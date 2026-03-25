package com.shibana.social_service.dto.response;

public record AuthorProfileResponse(
        String userId,
        String username,
        String displayName,

        String avatarMediaName,
        Double avatarScale,
        Double avatarPositionX,
        Double avatarPositionY
) {
}
