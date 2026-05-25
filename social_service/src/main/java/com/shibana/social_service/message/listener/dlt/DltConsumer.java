package com.shibana.social_service.message.listener.dlt;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class DltConsumer {
    @KafkaListener(
            topics = "identity.user.events.DLT",
            groupId = "social-service-dlt-group"
    )
    public void handleDltMessage(
            ConsumerRecord<String, String> record,
            @Header(value = KafkaHeaders.DLT_EXCEPTION_MESSAGE, required = false) byte[] errorMessageBytes,
//            @Header(KafkaHeaders.DLT_EXCEPTION_STACKTRACE, required = false) byte[ stackTraceBytes,
            @Header(value = KafkaHeaders.DLT_ORIGINAL_TOPIC, required = false) byte[] originalTopicBytes
    ) {
        String errorMessage = byteToString(errorMessageBytes);
        String originalTopic = byteToString(originalTopicBytes);

        log.error("🚨 [BÁO ĐỘNG ĐỎ] Nhận gói tin xịt từ Topic gốc: {}", originalTopic);
        log.error("🔑 Key: {}", record.key());
        log.error("📦 Payload xịt: {}", record.value());
        log.error("💥 Nguyên nhân cái chết: {}", cleanErrorMessage(errorMessage));
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
}
