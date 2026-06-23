package com.shibana.post_service.messaging.publisher.impl;

import com.github.f4b6a3.uuid.UuidCreator;
import com.shibana.post_service.messaging.dto.EventType;
import com.shibana.post_service.messaging.evenlope.EventEnvelope;
import com.shibana.post_service.messaging.evenlope.EventEnvelopeMetadata;
import com.shibana.post_service.messaging.helper.JsonHelper;
import com.shibana.post_service.messaging.publisher.EventPublisher;
import com.shibana.post_service.model.enums.AggregateTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class KafkaEventPublisher implements EventPublisher {
    JsonHelper jsonHelper;
    KafkaTemplate<String, String> kafkaTemplate;

    @NonFinal
    @Value("${infra.kafka.topics.reaction-event.name}")
    String reactionEventTopicName;

    @NonFinal
    @Value("${infra.kafka.producer.name}")
    String producerName;

    @Override
    public <T> void publishEvent(T eventPayload, AggregateTypeEnum aggregateType, String aggregateId, EventType eventType) {
        if (aggregateType == AggregateTypeEnum.REACTION) {
            log.info("Publishing reaction event to reaction-topic");
            EventEnvelopeMetadata metadata = EventEnvelopeMetadata.builder()
                    .eventId(UuidCreator.getTimeOrderedEpoch())
                    .eventType(eventType)
                    .timestamp(Instant.now())
                    .source(producerName)
                    .build();

            var eventEnvelope = EventEnvelope.builder()
                    .metadata(metadata)
                    .payload(eventPayload)
                    .build();

            String serializedEventPayload = jsonHelper.serialize(eventEnvelope);

            kafkaTemplate.send(reactionEventTopicName, aggregateId, serializedEventPayload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Bắn event thất bại cho target {}", aggregateId, ex);
                        } else {
                            log.info("Bắn event thành công vào Partition: {}, Key: {}",
                                    result.getRecordMetadata().partition(), aggregateId);
                        }
                    });
            ;
        }
    }

}
