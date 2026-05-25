package com.shibana.social_service.message.listener.internal;

import com.shibana.social_service.exception.AppException;
import com.shibana.social_service.message.outbox.entity.OutboxEvent;
import com.shibana.social_service.message.outbox.event.OutboxCreatedLocalEvent;
import com.shibana.social_service.message.outbox.service.OutboxService;
import com.shibana.social_service.message.publisher.EventPublisher;
import lombok.AccessLevel;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalOutboxEventListener {
    OutboxService outboxService;
    EventPublisher eventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePublishLocalEvent(OutboxCreatedLocalEvent event) {
        log.info("Handling publish local event");
        OutboxEvent outboxEvent;

        try {
            outboxEvent = outboxService.getPendingEvent(event.outboxId());
        } catch (AppException e) {
            log.warn(e.getMessage());
            return;
        }

        try {
            eventPublisher.publish(outboxEvent);
            outboxService.markEventAsCompleted(outboxEvent);
        } catch (AppException e) {
            log.error("Gửi Kafka thất bại. Chuẩn bị tăng Retry...", e);
            outboxService.markEventAsFaild(outboxEvent, 4);
        }
    }
}
