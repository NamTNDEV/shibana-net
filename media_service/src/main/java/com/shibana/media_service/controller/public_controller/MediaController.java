package com.shibana.media_service.controller.public_controller;

import com.shibana.media_service.dto.response.ApiResponse;
import com.shibana.media_service.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class MediaController {
    MediaService mediaService;

    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        log.info("Health check endpoint called");
        return ApiResponse.<String>builder()
                .code(200)
                .message("Media Service is up and running")
                .data("OK")
                .build();
    }
}
