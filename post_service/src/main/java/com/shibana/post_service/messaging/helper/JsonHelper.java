package com.shibana.post_service.messaging.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.messaging.dto.EventType;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class JsonHelper {
    ObjectMapper mapper;

    public EventType extractEventType(String rawJsonEvent) {
        try {
            var root = mapper.readTree(rawJsonEvent);
            if (root.has("metadata") && root.get("metadata").has("eventType")) {
                return EventType.valueOf(root.get("metadata").get("eventType").asText());
            }
            return EventType.UNKNOWN;
        } catch (IllegalArgumentException | JsonProcessingException e) {
            log.error("Failed to parse JSON event to EventType! Reason: {}", e.getMessage());
            throw new AppException(ErrorCode.INVALID_JSON_PARSING);
        }
    }

    public <T> T parsePayload(String rawJsonEvent, Class<T> targetClass) {
        try {
            var root = mapper.readTree(rawJsonEvent);
            return mapper.treeToValue(root.get("payload"), targetClass);
        } catch (IllegalArgumentException | JsonProcessingException e) {
            log.error("Failed to parse JSON event payload to {}! Reason: {}", targetClass.getSimpleName(), e.getMessage());
            throw new AppException(ErrorCode.INVALID_JSON_PARSING);
        }
    }
}
