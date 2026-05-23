package com.shibana.identity_service.message.outbox.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.shibana.identity_service.exception.AppException;
import com.shibana.identity_service.exception.ErrorCode;
import com.shibana.identity_service.message.dto.envelope.EventEnvelope;
import com.shibana.identity_service.message.dto.envelope.EventEnvelopeMetadata;
import com.shibana.identity_service.message.helper.JsonHelper;
import com.shibana.identity_service.message.outbox.dto.OutboxCreationRequest;
import com.shibana.identity_service.message.outbox.entity.OutboxEvent;
import com.shibana.identity_service.message.outbox.enums.OutboxStatus;
import com.shibana.identity_service.message.outbox.events.OutboxCreatedLocalEvent;
import com.shibana.identity_service.message.outbox.repo.OutboxRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OutboxService {
    OutboxRepo outboxRepo;
    JsonHelper jsonHelper;
    ApplicationEventPublisher appPublisher;

    @Value("${infra.kafka.producer.name}")
    @NonFinal
    String eventSourceName;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markEventAsFaild(OutboxEvent outboxEvent, int maxRetries) {
        outboxEvent.setRetryCount(outboxEvent.getRetryCount() + 1);

        if (outboxEvent.getRetryCount() >= maxRetries) {
            outboxEvent.setStatus(OutboxStatus.DEAD);
            log.error("CẢNH BÁO ĐỎ! Event {} đã DEAD sau {} lần thử", outboxEvent.getId(), maxRetries);
        } else {
            outboxEvent.setStatus(OutboxStatus.FAILED);
             log.warn("Event {} đánh dấu FAILED. Retry count: {}", outboxEvent.getId(), outboxEvent.getRetryCount());
        }
        outboxRepo.save(outboxEvent);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markEventAsPublished(OutboxEvent outboxEvent) {
        outboxEvent.setPublishedAt(Instant.now());
        outboxEvent.setStatus(OutboxStatus.COMPLETED);
        outboxRepo.save(outboxEvent);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,  readOnly = true)
    public OutboxEvent getPendingEvent(UUID outboxEventId) {
        return outboxRepo.findById(outboxEventId)
                .filter(event -> event.getStatus() == OutboxStatus.PENDING)
                .orElseThrow(() -> new AppException(ErrorCode.OUTBOX_EVENT_NOT_FOUND));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public <T> void creatAndPublishOutboxEvent(OutboxCreationRequest<T> outboxCreationRequest) {

        UUID eventId = UuidCreator.getTimeOrderedEpoch();
        var envelopeMetadata = new EventEnvelopeMetadata(
                eventId,
                eventSourceName,
                outboxCreationRequest.getEventType(),
                outboxCreationRequest.getCreatedAt(),
                outboxCreationRequest.getEventVersion()
        );

        var eventEnvelope = new EventEnvelope<>(
                envelopeMetadata,
                outboxCreationRequest.getEventPayload()
        );

        String deserializedPayload = jsonHelper.serialize(eventEnvelope);

        var outboxEvent = OutboxEvent.builder()
                .aggregateType(outboxCreationRequest.getAggregateType())
                .aggregateId(outboxCreationRequest.getAggregateId())
                .eventType(outboxCreationRequest.getEventType())
                .payload(deserializedPayload)
                .build();

        outboxRepo.save(outboxEvent);
        appPublisher.publishEvent(
                OutboxCreatedLocalEvent.builder()
                        .outboxEventId(outboxEvent.getId())
                        .build()
        );
    }
}
