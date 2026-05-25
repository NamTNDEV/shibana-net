package com.shibana.social_service.message.publisher.impl;

import com.shibana.social_service.message.dto.envelope.EventEnvelope;
import com.shibana.social_service.message.helper.JsonHelper;
import com.shibana.social_service.message.dto.envelope.EventEnvelopeMetadata;
import com.shibana.social_service.message.dto.EventType;
import com.shibana.social_service.message.dto.payloads.AvatarUpdatedPayload;
import com.shibana.social_service.message.publisher.ProfileMessagePublisher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaProfileMessagePublisher implements ProfileMessagePublisher {
    final JsonHelper jsonHelper;
    final KafkaTemplate<UUID, String> kafkaTemplate;

    @Value("${infra.kafka.producer.name}")
    String producerName;

    @Value("${infra.kafka.topics.profile-event.name}")
    String topicProfileUpdated;

    @Override
    public void publishAvatarUpdatedMessage(AvatarUpdatedPayload payload) {
        EventEnvelopeMetadata metadata = EventEnvelopeMetadata.builder()
                .eventId(UUID.randomUUID().toString())
                .source(producerName)
                .eventType(EventType.AVATAR_UPDATED)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        EventEnvelope<AvatarUpdatedPayload> envelope = EventEnvelope
                .<AvatarUpdatedPayload>builder()
                .metadata(metadata)
                .payload(payload)
                .build();

        String jsonMessage = jsonHelper.serialize(envelope);
        kafkaTemplate.send(topicProfileUpdated, payload.getUserId(), jsonMessage);
    }
}
