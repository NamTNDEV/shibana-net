package com.shibana.identity_service.message.publisher;

import com.shibana.identity_service.message.outbox.entity.OutboxEvent;

public interface EventPublisher {
    void publish(OutboxEvent outboxEvent);
}
