package com.shibana.media_service.controller.internal_controller;

import com.shibana.media_service.dto.response.ApiResponse;
import com.shibana.media_service.dto.response.UploadedMediaResponse;
import com.shibana.media_service.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/upload")
    public  ApiResponse<UploadedMediaResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String authorId = jwt.getClaimAsString("user_id");
        return ApiResponse.<UploadedMediaResponse>builder()
                .code(200)
                .data(mediaService.uploadFile(file, authorId))
                .message("File uploaded successfully")
                .build();
    }
}
