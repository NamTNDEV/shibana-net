package com.shibana.notification_service.message.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {
    @KafkaListener(topics = "${app.kafka.topics.test}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        log.info("✅ Received message from Kafka:: {}", message);
    }
}
