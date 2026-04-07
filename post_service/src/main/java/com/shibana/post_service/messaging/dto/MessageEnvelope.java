package com.shibana.post_service.messaging.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageEnvelope<T> {
    MessageEnvelopeMetadata metadata;
    T payload;
}
