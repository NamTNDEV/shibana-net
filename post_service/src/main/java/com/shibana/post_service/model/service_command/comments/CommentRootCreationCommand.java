package com.shibana.post_service.model.service_command.comments;

import java.util.UUID;

public record CommentRootCreationCommand(
        UUID postId,
        String content,
        UUID commnentorId
) {
}
