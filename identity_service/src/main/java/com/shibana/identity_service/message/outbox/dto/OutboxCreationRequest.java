package com.shibana.identity_service.message.outbox.dto;

import com.shibana.identity_service.message.dto.EventType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutboxCreationRequest<T> {
    String aggregateType;
    String aggregateId;
    T eventPayload;
    EventType eventType;
    long eventVersion;
    Instant createdAt;
}
