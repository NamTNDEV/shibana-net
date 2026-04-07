package com.shibana.social_service.messaging.event;

import com.shibana.social_service.messaging.dto.payloads.AvatarUpdatedPayload;

public record AvatarUpdatedLocalEvent(
        AvatarUpdatedPayload payload
) {
}
