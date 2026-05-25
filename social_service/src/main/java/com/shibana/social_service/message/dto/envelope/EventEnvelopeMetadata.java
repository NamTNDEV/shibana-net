package com.shibana.social_service.message.dto.envelope;

import com.shibana.social_service.message.dto.EventType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class EventEnvelopeMetadata {
    UUID eventId;
    String source;
    EventType eventType;
    Instant timestamp;
}
