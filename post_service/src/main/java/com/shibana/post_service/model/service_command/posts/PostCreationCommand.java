package com.shibana.post_service.model.service_command.posts;

import com.shibana.post_service.model.enums.PostPrivacyEnum;

import java.util.UUID;

public record PostCreationCommand(
        String content,
        UUID authorId,
        PostPrivacyEnum privacy
) {
}
