package com.shibana.social_service.message.publisher.impl;

import com.shibana.social_service.exception.AppException;
import com.shibana.social_service.exception.ErrorCode;
import com.shibana.social_service.message.outbox.entity.OutboxEvent;
import com.shibana.social_service.message.publisher.EventPublisher;
import com.shibana.social_service.properties.InfraKafkaProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class KafkaEventPublisher implements EventPublisher {
    InfraKafkaProperties kafkaProperties;
    KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publish(OutboxEvent outboxEvent) {
        Map<String, String> outboxRouting = kafkaProperties.getOutboxRouting();
        String topic = outboxRouting.get(outboxEvent.getAggregateType());

        if (topic == null) {
            throw new AppException(ErrorCode.TOPIC_NOT_FOUND);
        }

        try {
            kafkaTemplate.send(
                            topic,
                            outboxEvent.getAggregateId(),
                            outboxEvent.getEventPayload())
                    .get(5, TimeUnit.SECONDS)
            ;
        } catch (Exception e) {
            log.error("Lỗi khi bắn Kafka Event ID: {}", outboxEvent.getId(), e);
            throw new AppException(ErrorCode.KAFKA_PUBLISH_FAILED);
        }
    }
}
