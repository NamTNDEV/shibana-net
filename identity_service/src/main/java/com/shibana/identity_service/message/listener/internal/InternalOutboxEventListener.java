package com.shibana.identity_service.message.listener.internal;

import com.shibana.identity_service.exception.AppException;
import com.shibana.identity_service.message.outbox.entity.OutboxEvent;
import com.shibana.identity_service.message.outbox.events.OutboxCreatedLocalEvent;
import com.shibana.identity_service.message.outbox.service.OutboxService;
import com.shibana.identity_service.message.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class InternalOutboxEventListener {
    OutboxService outboxService;
    EventPublisher publisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOutboxCreatedEvent(OutboxCreatedLocalEvent localEvent) {
        OutboxEvent outboxEvent;
        try {
            outboxEvent = outboxService.getPendingEvent(localEvent.getOutboxEventId());
        } catch (AppException error) {
            log.warn(error.getMessage());
            return;
        }

        try {
            publisher.publish(outboxEvent);
            outboxService.markEventAsPublished(outboxEvent);
        } catch (AppException e) {
            log.error("Gửi Kafka thất bại. Chuẩn bị tăng Retry...", e);
            outboxService.markEventAsFaild(outboxEvent, 4);
        }
    }
}
