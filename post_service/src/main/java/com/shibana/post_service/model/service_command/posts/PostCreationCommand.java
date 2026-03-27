package com.shibana.post_service.model.service_command.posts;

import com.shibana.post_service.model.enums.PostPrivacyEnum;

public record PostCreationCommand(
        String content,
        String authorId,
        PostPrivacyEnum privacy
) {
}
