package com.shibana.identity_service.message.dto.envelope;

public record EventEnvelope<T>(
        EventEnvelopeMetadata metadata,
        T payload
) {
}
