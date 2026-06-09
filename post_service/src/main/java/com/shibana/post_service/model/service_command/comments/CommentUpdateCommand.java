package com.shibana.post_service.model.service_command.comments;

import java.util.UUID;

public record CommentUpdateCommand(
        UUID updaterId,
        UUID commentId,
        String content
) {
}
