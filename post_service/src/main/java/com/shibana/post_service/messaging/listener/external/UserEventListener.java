package com.shibana.post_service.messaging.listener.external;

import com.shibana.post_service.messaging.dto.EventType;
import com.shibana.post_service.messaging.dto.payloads.CachedUserRegisteredPayload;
import com.shibana.post_service.messaging.helper.JsonHelper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserEventListener {
    JsonHelper jsonHelper;

    @KafkaListener(
            topics = "${infra.kafka.topics.user-event}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleUserEvents(ConsumerRecord<String, String> rawJsonEvent) {
        EventType eventType = jsonHelper.extractEventType(rawJsonEvent.value());
        try {
            switch (eventType) {
                case USER_REGISTERED -> {
                    var payload = jsonHelper.parsePayload(rawJsonEvent.value(), CachedUserRegisteredPayload.class);
                    log.info("Received USER REGISTERED event");
                }
                default -> log.warn("⚠\uFE0F Received unsupported event type: {}", eventType);
            }
        } catch (DataIntegrityViolationException | OptimisticLockingFailureException e) {
            log.warn("🚨 Phát hiện event trùng lặp hoặc vi phạm constraint dữ liệu. Đã an toàn bỏ qua!");
        }
    }
}
