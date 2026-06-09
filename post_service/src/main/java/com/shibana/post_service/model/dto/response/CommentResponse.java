package com.shibana.post_service.model.dto.response;

import java.util.Map;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID parentId,
        int level,

        String content,

        int replyCount,
        Map<String, Integer> reactionStats,
        boolean isEdited,

        AuthorResponse author
) {
}
