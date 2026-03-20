package com.shibana.social_service.dto;

public record ConnectionStatus(
        boolean isFollowing,
        boolean isFriended,
        boolean hasSentFriendRequest) {
}
