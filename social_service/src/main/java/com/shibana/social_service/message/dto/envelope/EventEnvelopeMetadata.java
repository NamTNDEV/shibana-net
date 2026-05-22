package com.shibana.social_service.message.dto.envelope;

import com.shibana.social_service.message.dto.EventType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class EventEnvelopeMetadata {
    String eventId;
    String source;
    EventType eventType;
    long  timestamp;
}
