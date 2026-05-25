package com.shibana.identity_service.jobs;

import com.shibana.identity_service.message.outbox.repo.OutboxRepo;
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
public class OutboxCleanEventsScheduler {
    OutboxRepo outboxRepository;

    @Scheduled(cron = "0 0 1 * * ?") // Runs every day at midnight
    public void cleanOutboxEvents() {
        log.info("🧹 [IDENTITY] Bắt đầu dọn dẹp nhà kho Outbox...");

        // Tính mốc thời gian cách đây 3 ngày
        Instant threshold = Instant.now().minus(3, ChronoUnit.DAYS);

        int deletedRows = outboxRepository.deleteOldCompletedEvents(threshold);

        log.info("✨ [IDENTITY] Dọn dẹp xong! Đã tiêu hủy vĩnh viễn {} completed events cũ.", deletedRows);
    }
}
