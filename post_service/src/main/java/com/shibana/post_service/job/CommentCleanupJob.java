package com.shibana.post_service.job;

import com.shibana.post_service.repo.CommentRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentCleanupJob {
//    CommentRepo commentRepo;

        @Scheduled(cron = "0 0 2 * * ?")
//    @Scheduled(fixedRate = 5000)
    public void cleanComments() {
        log.info("[CRON-JOB] Bắt đầu quét rác DB: Xóa vĩnh viễn Comment mồ côi quá 30 ngày...");

        try {
            Instant cutoffTime = Instant.now().minus(30, ChronoUnit.DAYS);
//            Instant cutoffTime = Instant.now().minus(10, ChronoUnit.SECONDS);
//            int deletedCount = commentRepo.hardDeleteOldDeletedComment(cutoffTime);
//            log.info("[CRON-JOB] Dọn dẹp hoàn tất! Đã hóa vàng {} comment rác.", deletedCount);
        } catch (Exception e) {
            log.error("[CRON-JOB] Lỗi trong quá trình dọn dẹp rác: ", e);
        }
    }
}
