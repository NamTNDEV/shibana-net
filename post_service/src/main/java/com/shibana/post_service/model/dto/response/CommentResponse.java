package com.shibana.post_service.model.dto.response;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID parentId,
        UUID postId,
        int level,

        String content,
        Instant createdAt,

        int replyCount,
        Map<String, Integer> reactionStats,
        boolean isEdited,

        AuthorResponse author
) {
}
