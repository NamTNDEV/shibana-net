package com.shibana.post_service.model.dto.response;

public record CommentResponse(
        String id,
        String content,
        int replyCount,
        int level,
        String postId,

        AuthorResponse author
) {
}
