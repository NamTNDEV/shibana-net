package com.shibana.post_service.controller;

import com.shibana.post_service.dto.response.ApiResponse;
import com.shibana.post_service.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class PostController {
    PostService postService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/health-check")
    public ApiResponse<?> healthCheck() {
        return ApiResponse.<String>builder()
                .code(200)
                .message("Post Service is up and running!")
                .build();
    }
}
