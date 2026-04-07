package com.shibana.social_service.messaging.listener.internal;

import com.shibana.social_service.messaging.event.AvatarUpdatedLocalEvent;
import com.shibana.social_service.messaging.publisher.impl.KafkaProfileMessagePublisher;
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
public class InternalProfileEventListener {
    KafkaProfileMessagePublisher kafkaProfileMessagePublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAvatarUpdatedLocalEvent(AvatarUpdatedLocalEvent event) {
        try {
            kafkaProfileMessagePublisher.publishAvatarUpdatedMessage(event.payload());
        } catch (Exception e) {
            log.error(":: ❌ Lỗi bắn Kafka sau khi lưu DB: {} ::", e.getMessage(), e);
        }
    }
}
