package com.shibana.post_service.utils;

import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class UuidUtils {
    static public UUID generateFromString(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw new AppException(ErrorCode.INVAKID_POST_ID);
        }
    }
}
