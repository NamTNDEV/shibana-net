package com.shibana.post_service.model.service_command.comments;

public record CommentCreationCommand(
        String postId,
        String content,
        String parentId,
        String commnentorId
) {
}
