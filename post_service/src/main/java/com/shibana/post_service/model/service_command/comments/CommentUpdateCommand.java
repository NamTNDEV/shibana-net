package com.shibana.post_service.model.service_command.comments;

public record CommentUpdateCommand(
        String updaterId,
        String commentId,
        String content
) {
}
