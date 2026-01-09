package com.shibana.identity_service.message.producer;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class KafkaPublisher {
    KafkaTemplate<String, String> kafkaTemplate;

    @NonFinal
    @Value("${app.kafka.topics.test}")
    String TEST_TOPIC;

    public void publishTestMessage(String message) {
        log.info("Publishing message to Kafka topic {}: {}", TEST_TOPIC, message);
        kafkaTemplate.send(TEST_TOPIC, message);
    }
}
