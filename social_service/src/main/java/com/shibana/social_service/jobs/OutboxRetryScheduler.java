package com.shibana.social_service.jobs;

import com.shibana.social_service.message.outbox.entity.OutboxEvent;
import com.shibana.social_service.message.outbox.repo.OutboxRepo;
import com.shibana.social_service.message.outbox.service.OutboxService;
import com.shibana.social_service.message.publisher.EventPublisher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OutboxRetryScheduler {
    OutboxRepo outboxRepo;
    OutboxService outboxService;
    EventPublisher eventPublisher;

    @Scheduled(fixedDelay = 6000)
    public void proccessFailedEvents() {
        Instant pendingTimeout = Instant.now().minusSeconds(60);
        int maxRetries = 3;
        int batchSize = 50;
        PageRequest page = PageRequest.of(0, batchSize);

        List<OutboxEvent> failedEvents = new ArrayList<>(
                outboxRepo.findFailedEvents(maxRetries, page)
        );

        if (failedEvents.isEmpty() || failedEvents.size() < batchSize) {
            failedEvents.addAll(outboxRepo.findPendingEvents(pendingTimeout, page));
        }

        if (failedEvents.isEmpty()) {
            return;
        }

        failedEvents.forEach(event -> {
            try {
                eventPublisher.publish(event);
                outboxService.markEventAsCompleted(event);
            } catch (Exception e) {
                log.error("Failed to publish event {}:: {}", event.getId(), e.getMessage());
                outboxService.markEventAsFaild(event, maxRetries);
            }
        });
    }
}
