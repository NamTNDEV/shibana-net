package com.shibana.post_service.messaging.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.messaging.dto.MessageType;
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

    public MessageType extractEventType(String jsonMessage) {
        try {
            JsonNode root = mapper.readTree(jsonMessage);
            if (root.has("metadata") && root.get("metadata").has("messageType")) {
                return MessageType.valueOf(root.get("metadata").get("messageType").asText());
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON message: {}", jsonMessage, e);
            throw new AppException(ErrorCode.INVALID_JSON_PARSING);
        }
        return MessageType.UNKNOWN;
    }

    public <T> T parsePayload(String jsonMessage, Class<T> targetClass) {
        try {
            JsonNode root = mapper.readTree(jsonMessage);
            return mapper.treeToValue(root.get("payload"), targetClass);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON message: {}", jsonMessage, e);
            throw new AppException(ErrorCode.INVALID_JSON_PARSING);
        }
    }
}
