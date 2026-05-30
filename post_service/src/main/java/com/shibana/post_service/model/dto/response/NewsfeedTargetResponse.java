package com.shibana.post_service.model.dto.response;

import java.util.Set;
import java.util.UUID;

public record NewsfeedTargetResponse(
        Set<UUID> friendIds,
        Set<UUID> followingIds
) {
}
