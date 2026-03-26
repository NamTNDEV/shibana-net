package com.shibana.post_service.model.dto.response;

import java.util.Set;

public record NewsfeedTargetResponse(
        Set<String> friendIds,
        Set<String> followingIds
) {
}
