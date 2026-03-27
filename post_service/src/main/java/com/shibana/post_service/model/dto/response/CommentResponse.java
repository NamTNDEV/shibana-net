package com.shibana.post_service.model.dto.response;

import com.shibana.post_service.model.embedded.Author;

public record CommentResponse(
        String id,
        String postId,
        Author author,
        String content,
        int replyCount,
        int level
) {
}
