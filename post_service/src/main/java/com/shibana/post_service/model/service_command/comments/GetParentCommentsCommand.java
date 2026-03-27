package com.shibana.post_service.model.service_command.comments;

public record GetParentCommentsCommand(
        int page,
        int size,
        String postId
) {
}
