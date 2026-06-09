package com.shibana.post_service.model.service_command.comments;

import java.util.UUID;

public record ReplyCommentCreationCommand(
        String content,
        UUID parentId,
        UUID commnentorId
) {
}
