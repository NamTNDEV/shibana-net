package com.shibana.social_service.message.publisher;

import com.shibana.social_service.message.outbox.entity.OutboxEvent;

public interface EventPublisher {
    void publish(OutboxEvent outboxEvent);
}
