package com.shibana.social_service.messaging.publisher;

import com.shibana.social_service.messaging.dto.payloads.AvatarUpdatedPayload;

public interface ProfileMessagePublisher {
    void publishAvatarUpdatedMessage(AvatarUpdatedPayload payload);
}
