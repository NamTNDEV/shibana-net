package com.shibana.post_service.messaging.publisher;


import com.shibana.post_service.messaging.dto.EventType;
import com.shibana.post_service.model.enums.AggregateTypeEnum;

public interface EventPublisher {
    <T> void publishEvent(T eventPayload, AggregateTypeEnum aggregateType, String aggregateId,EventType eventType);
}
