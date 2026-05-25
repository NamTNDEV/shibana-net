package com.shibana.social_service.jobs;

import com.shibana.social_service.message.outbox.repo.OutboxRepo;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class OutboxEventsCleanScheduler {
    OutboxRepo outboxRepo;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanUpCompletedGraphEvents() {
        log.info("🧹 [SOCIAL] Con BOT dọn dẹp đồ thị Outbox thức dậy...");

        Instant threshold = Instant.now().minus(3, ChronoUnit.DAYS);

        outboxRepo.deleteOldCompletedEvents(threshold);

        log.info("✨ [SOCIAL] Đồ thị đã được làm sạch! Đã xóa toàn bộ Completed Node quá 3 ngày.");
    }
}
