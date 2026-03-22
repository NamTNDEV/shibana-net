package com.shibana.social_service.dto;

import com.shibana.social_service.enums.friendship_status.FriendshipStatus;

public record RelationshipContext(
    boolean isFollowing,
    FriendshipStatus friendshipStatus
) {
}
