package com.shibana.social_service.dto.response;

import java.util.Set;

public record NewsfeedTargetResponse(
        Set<String> friendIds,
        Set<String> followingIds
) {
}
