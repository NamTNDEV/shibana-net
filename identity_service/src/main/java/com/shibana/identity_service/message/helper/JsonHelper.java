package com.shibana.identity_service.message.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shibana.identity_service.exception.AppException;
import com.shibana.identity_service.exception.ErrorCode;
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
        } catch (Exception e) {
            log.error("Failed to serialize object: {}", e.getMessage());
            throw new AppException(ErrorCode.SERIALIZATION_ERROR);
        }
    }
}
