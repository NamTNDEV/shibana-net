package com.shibana.post_service.messaging.listener.dlt;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DltConsumer {
    @KafkaListener(
            topics = "#{'${infra.kafka.dlt_topics}'.split(', ')}",
            groupId = "post-service-dlt-group"
    )
    public void handleUserDltMessage(
            ConsumerRecord<String, String> record,
            @Header(value = KafkaHeaders.DLT_EXCEPTION_MESSAGE, required = false) byte[] errorMessageBytes,
//            @Header(KafkaHeaders.DLT_EXCEPTION_STACKTRACE, required = false) byte[ stackTraceBytes,
            @Header(value = KafkaHeaders.DLT_ORIGINAL_TOPIC, required = false) byte[] originalTopicBytes
    ) {
        String originalTopic = byteToString(originalTopicBytes);
        String errorMessage = byteToString(errorMessageBytes);
        String serializedDltMessage = cleanErrorMessage(errorMessage);

        processLogs(originalTopic, record, serializedDltMessage);
    }

    private String cleanErrorMessage(String rawMessage) {
        if (rawMessage == null) return "No error message provided";
        String delimiter = "threw exception; ";
        int index = rawMessage.indexOf(delimiter);
        if (index == -1) return rawMessage;
        return rawMessage.substring(index + delimiter.length()).trim();
    }

    private String byteToString(byte[] bytes) {
        return bytes != null ? new String(bytes, StandardCharsets.UTF_8) : "N/A";
    }

    private String maskSensitiveFields(String payload) {
        if (payload == null) return "null";
        return payload
                .replaceAll("\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***\"")
                .replaceAll("\"token\"\\s*:\\s*\"[^\"]*\"", "\"token\":\"***\"");
    }

    private void processLogs(String originalTopic, ConsumerRecord<String, String> record, String errorMessage) {
        log.error("""
                        \n
                        ╔══════════════════════════════════════════╗
                        ║           DEAD LETTER TOPIC ALERT        ║
                        ╚══════════════════════════════════════════╝
                        📍 Original Topic : {}
                        🔑 Key             : {}
                        📋 Partition       : {}
                        📦 Offset          : {}
                        ⏰ Timestamp       : {}
                        ─────────────────────────────────────────
                        📄 Payload:
                        {}
                        ─────────────────────────────────────────
                        💥 Error:
                        {}
                        ══════════════════════════════════════════
                        """,
                originalTopic,
                record.key(),
                record.partition(),
                record.offset(),
                Instant.ofEpochMilli(record.timestamp()),
                maskSensitiveFields(record.value()),
                errorMessage
        );
    }
}
