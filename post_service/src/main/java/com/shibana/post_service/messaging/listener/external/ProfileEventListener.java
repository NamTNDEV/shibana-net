package com.shibana.post_service.messaging.listener.external;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ProfileEventListener {

    @KafkaListener(
            topics = "${infra.kafka.topics.profile-event}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleProfileEvents(ConsumerRecord<String, String> rawJsonEvent) {
        log.info("Profile event received:: {}", rawJsonEvent);
    }
}
