package com.shibana.media_service.controller.internal_controller;

import com.shibana.media_service.dto.response.ApiResponse;
import com.shibana.media_service.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/internal")
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class InternalMediaController {
    MediaService mediaService;

    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        mediaService.testService();
        return ApiResponse.<String>builder()
                .code(200)
                .message("Internal Media Service is up and running")
                .data("OK")
                .build();
    }


}
