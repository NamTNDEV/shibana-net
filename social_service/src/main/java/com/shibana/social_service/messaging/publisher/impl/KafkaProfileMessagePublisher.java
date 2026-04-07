package com.shibana.social_service.messaging.publisher.impl;

import com.shibana.social_service.messaging.helper.JsonHelper;
import com.shibana.social_service.messaging.dto.MessageEnvelope;
import com.shibana.social_service.messaging.dto.MessageEnvelopeMetadata;
import com.shibana.social_service.messaging.dto.MessageType;
import com.shibana.social_service.messaging.dto.payloads.AvatarUpdatedPayload;
import com.shibana.social_service.messaging.publisher.ProfileMessagePublisher;
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
    final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${infra.kafka.procedure.name}")
    String procedureName;

    @Value("${infra.kafka.topics.avatar-updated}")
    String topicProfileUpdated;

    @Override
    public void publishAvatarUpdatedMessage(AvatarUpdatedPayload payload) {
        MessageEnvelopeMetadata metadata = MessageEnvelopeMetadata.builder()
                .messageId(UUID.randomUUID().toString())
                .source(procedureName)
                .messageType(MessageType.AVATAR_UPDATED)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        MessageEnvelope<AvatarUpdatedPayload> envelope = MessageEnvelope
                .<AvatarUpdatedPayload>builder()
                .metadata(metadata)
                .payload(payload)
                .build();

        String jsonMessage = jsonHelper.serialize(envelope);
        kafkaTemplate.send(topicProfileUpdated, payload.getUserId(), jsonMessage);
    }
}
