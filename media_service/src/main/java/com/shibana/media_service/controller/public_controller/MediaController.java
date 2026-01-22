package com.shibana.media_service.controller.public_controller;

import com.shibana.media_service.dto.response.ApiResponse;
import com.shibana.media_service.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class MediaController {
    MediaService mediaService;
    @GetMapping("/static/{fileName:.+}")
    public ApiResponse<Void> staticFile(@PathVariable String fileName) {
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Static file access endpoint placeholder")
                .build();
    }
}
