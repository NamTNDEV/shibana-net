package com.shibana.identity_service.message.dto.envelope;

import com.shibana.identity_service.message.dto.EventType;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelopeMetadata(
        UUID eventId,
        String source,
        EventType eventType,
        Instant timestamp,
        long eventVersion
) {
}
