package com.shibana.social_service.message.dto.envelope;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class EventEnvelope<T> {
    EventEnvelopeMetadata metadata;
    T payload;
}
