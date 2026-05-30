package com.shibana.social_service.dto.response;

import java.util.Set;
import java.util.UUID;

public record NewsfeedTargetResponse(
        Set<UUID> friendIds,
        Set<UUID> followingIds
) {
}
