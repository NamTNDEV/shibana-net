package com.shibana.social_service.dto;

public record RelationshipContext(
    boolean isFriended,
    boolean isFollowing,
    boolean hasSentFriendRequest
) {
}
