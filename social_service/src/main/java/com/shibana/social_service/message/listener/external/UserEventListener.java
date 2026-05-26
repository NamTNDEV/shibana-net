package com.shibana.social_service.message.listener.external;

import com.shibana.social_service.dto.request.ProfileCreationRequest;
import com.shibana.social_service.message.dto.EventType;
import com.shibana.social_service.message.helper.JsonHelper;
import com.shibana.social_service.service.ProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserEventListener {
    ProfileService profileService;
    JsonHelper jsonHelper;

    @KafkaListener(
            topics = "${infra.kafka.topics.user-event}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleUserEvents(ConsumerRecord<String, String> rawJson) {
        EventType eventType = jsonHelper.extractEventType(rawJson.value());
        try {
            switch (eventType) {
                case USER_REGISTERED -> {
                    ProfileCreationRequest requestPayload = jsonHelper.parsePayload(rawJson.value(), ProfileCreationRequest.class);
                    profileService.createProfile(requestPayload);
                }
                default -> log.warn("Received unsupported event type: {}", eventType);
            }
        } catch (DataIntegrityViolationException | OptimisticLockingFailureException e) {
            log.warn("🚨 Phát hiện event trùng lặp hoặc vi phạm constraint dữ liệu. Đã an toàn bỏ qua!");
        }
    }
}
