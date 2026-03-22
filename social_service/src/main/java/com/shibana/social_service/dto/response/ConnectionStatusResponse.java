package com.shibana.social_service.dto.response;

import com.shibana.social_service.enums.friendship_status.FriendshipStatus;

public record ConnectionStatusResponse(
        boolean isFollowing,
        FriendshipStatus friendshipStatus
) {
}
