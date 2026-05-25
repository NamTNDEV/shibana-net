package com.shibana.social_service.message.outbox.dto;

import com.shibana.social_service.message.dto.EventType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutboxCreationRequest<T> {
    String aggregateType;
    String aggregateId;
    T eventPayload;
    EventType eventType;
    Instant createdAt;
}
