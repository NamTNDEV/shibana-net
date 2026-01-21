package com.shibana.media_service.service;

import com.shibana.media_service.exception.AppException;
import com.shibana.media_service.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class MediaService {
    public void testService() {
        log.info("MediaService testService called");
    }
}
