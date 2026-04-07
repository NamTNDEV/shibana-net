package com.shibana.social_service.messaging.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MessageEnvelopeMetadata {
    String messageId;
    String source;
    MessageType messageType;
    long  timestamp;
}
