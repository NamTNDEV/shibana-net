package com.shibana.social_service.message.publisher;

import com.shibana.social_service.message.dto.payloads.AvatarUpdatedPayload;

public interface ProfileMessagePublisher {
    void publishAvatarUpdatedMessage(AvatarUpdatedPayload payload);
}
