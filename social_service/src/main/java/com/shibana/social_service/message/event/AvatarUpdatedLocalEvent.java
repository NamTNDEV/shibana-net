package com.shibana.social_service.message.event;

import com.shibana.social_service.message.dto.payloads.AvatarUpdatedPayload;

public record AvatarUpdatedLocalEvent(
        AvatarUpdatedPayload payload
) {
}
