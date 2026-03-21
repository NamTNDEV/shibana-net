package com.shibana.social_service.dto.response;

public record ConnectionStatusResponse(
        boolean isFollowing,
        boolean isFriended,
        boolean hasSentFriendRequest) {
}
