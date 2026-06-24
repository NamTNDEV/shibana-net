package com.shibana.post_service.messaging.publisher.impl;

import com.github.f4b6a3.uuid.UuidCreator;
import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.messaging.dto.EventType;
import com.shibana.post_service.messaging.evenlope.EventEnvelope;
import com.shibana.post_service.messaging.evenlope.EventEnvelopeMetadata;
import com.shibana.post_service.messaging.helper.JsonHelper;
import com.shibana.post_service.messaging.publisher.EventPublisher;
import com.shibana.post_service.model.enums.AggregateTypeEnum;
import com.shibana.post_service.properties.InfraKafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class KafkaEventPublisher implements EventPublisher {
    JsonHelper jsonHelper;
    InfraKafkaProperties infraKafkaProperties;
    Map<String, KafkaTemplate<Object, Object>> kafkaTemplates;

    @NonFinal
    @Value("${infra.kafka.producer.name}")
    String producerName;

    @Override
    public <T> void publishEvent(T eventPayload, AggregateTypeEnum aggregateType, String aggregateId, EventType eventType) {
        var targetTopic = infraKafkaProperties.getOutboxRouting().get(aggregateType);
        if (targetTopic == null) {
            log.error("🚨[publishEvent]::Chưa cấu hình tên Topic cho Aggregate [{}] trong outbox-routing!", aggregateType);
            throw new AppException(ErrorCode.MISSING_TOPIC_ROUTING);
        }

        String templateBeanName = "reactionKafkaTemplate";
        if(infraKafkaProperties.getTemplateRouting() != null && infraKafkaProperties.getTemplateRouting().containsKey(aggregateType)) {
            templateBeanName = infraKafkaProperties.getTemplateRouting().get(aggregateType);
        }
        KafkaTemplate<Object, Object> kafkaTemplate = kafkaTemplates.get(templateBeanName);

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

        log.info("🚀 [Routing System] Đẩy Event [{}] của [{}] -> Topic: [{}], dùng Bean: [{}]",
                eventType, aggregateType, targetTopic, templateBeanName);

        kafkaTemplate.send(targetTopic, aggregateId, serializedEventPayload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("\uD83D\uDCA5 Bắn event thất bại cho target {}", aggregateId, ex);
                    } else {
                        log.info("✅Bắn event thành công vào Partition: {}, Key: {}",
                                result.getRecordMetadata().partition(), aggregateId);
                    }
                });
        ;
    }
}
