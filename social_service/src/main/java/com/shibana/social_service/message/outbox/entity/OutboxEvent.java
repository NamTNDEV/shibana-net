package com.shibana.social_service.message.outbox.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.shibana.social_service.message.dto.EventType;
import com.shibana.social_service.message.outbox.enums.OutboxStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Node("outbox_events")
public class OutboxEvent {
    @Id
    @Builder.Default
    UUID id  = UuidCreator.getTimeOrderedEpoch();

    String aggregateType;
    String aggregateId;

    String eventPayload;

    @Builder.Default
    OutboxStatus status = OutboxStatus.PENDING;

    EventType eventType;

    @Builder.Default
    int retryCount = 0;

    Instant createdAt;
    Instant publishedAt;

    @Version
    long version;
}
