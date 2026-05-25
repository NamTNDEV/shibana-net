package com.shibana.social_service.message.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shibana.social_service.exception.AppException;
import com.shibana.social_service.exception.ErrorCode;
import com.shibana.social_service.message.dto.EventType;
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

    public String serialize(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object: {}", e.getMessage());
            throw new AppException(ErrorCode.SERIALIZATION_ERROR);
        }
    }

    public EventType extractEventType(String jsonEvent) {
        try {
            var root = mapper.readTree(jsonEvent);
            if (root.has("metadata") && root.get("metadata").has("eventType")) {
                return EventType.valueOf(root.get("metadata").get("eventType").asText());
            }
            return EventType.UNKNOWN;
        } catch (JsonProcessingException e) {
            log.error("Failed to extract event type: {}", e.getMessage());
            throw new AppException(ErrorCode.INVALID_JSON_PARSING);
        }
    }

    public <T> T parsePayload(String jsonEvent, Class<T> targetClass) {
        try {
            var root = mapper.readTree(jsonEvent);
            return mapper.treeToValue(root.get("payload"), targetClass);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON message: {}", e.getMessage());
            throw new AppException(ErrorCode.INVALID_JSON_PARSING);
        }
    }
}
