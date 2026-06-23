package com.shibana.post_service.messaging.evenlope;

import com.shibana.post_service.messaging.dto.EventType;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventEnvelopeMetadata {
    UUID eventId;
    String source;
    EventType eventType;
    Instant timestamp;
}
