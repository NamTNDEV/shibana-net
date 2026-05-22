package com.shibana.identity_service.message.outbox.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.shibana.identity_service.message.dto.EventType;
import com.shibana.identity_service.message.outbox.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table(
        name = "outbox_events",
        indexes = {
                @Index(name = "idx_outbox_events_scheduler", columnList = "status, created_at"),
        }
)
public class OutboxEvent {
    @Id
    @Builder.Default
    UUID id = UuidCreator.getTimeOrderedEpoch();

    @Column(name = "aggregate_type", nullable = false)
    String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    String aggregateId;

    @ToString.Exclude
    @Column(columnDefinition = "TEXT", nullable = false)
    String payload;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    OutboxStatus status = OutboxStatus.PENDING;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    EventType eventType;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    int retryCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    Instant createdAt;

    @Column(name = "published_at")
    Instant publishedAt;

    @Version
    long version;
}
