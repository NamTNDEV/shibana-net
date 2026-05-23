package com.shibana.identity_service.jobs;

import com.shibana.identity_service.message.outbox.entity.OutboxEvent;
import com.shibana.identity_service.message.outbox.repo.OutboxRepo;
import com.shibana.identity_service.message.outbox.service.OutboxService;
import com.shibana.identity_service.message.publisher.EventPublisher;
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
    EventPublisher eventPublisher;
    OutboxService outboxService;

    @Scheduled(fixedDelay = 60000) // Chạy sau mỗi 30 giây
    public void processFailedEvents() {
        Instant pendingTimeout = Instant.now().minusSeconds(60);
        int maxRetries = 3;
        int batchSize = 50;
        PageRequest page = PageRequest.of(0, batchSize);

        List<OutboxEvent> eventsToRetry = new ArrayList<>(
                outboxRepo.findFailedEvents(maxRetries, page)
        );

        if (eventsToRetry.size() < batchSize) {
            int remainingSize = batchSize - eventsToRetry.size();
            eventsToRetry.addAll(outboxRepo.findStuckPendingEvent(pendingTimeout, PageRequest.of(0, remainingSize)));
        }

        if (eventsToRetry.isEmpty()) {
            log.info(":: No more events to retry ::");
            return;
        }

        log.info(":: Retrying {} events ::", eventsToRetry.size());
        eventsToRetry.forEach(event -> {
            try {
                eventPublisher.publish(event);
                outboxService.markEventAsPublished(event);
            } catch (Exception e) {
                log.error("Failed to publish event {}:: {}", event.getId(), e.getMessage());
                outboxService.markEventAsFaild(event, maxRetries);
            }
        });
    }
}
