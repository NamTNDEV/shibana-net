package com.shibana.social_service.controller.public_controller;

import com.shibana.social_service.dto.response.ApiResponse;
import com.shibana.social_service.enums.PrivacyLevel;
import com.shibana.social_service.service.PrivacyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/privacies")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivacyController {
    PrivacyService privacyService;

    @GetMapping("")
    public ApiResponse<List<PrivacyLevel>> getPrivacies() {
        return ApiResponse
                .<List<PrivacyLevel>>builder()
                .code(200)
                .message("Privacies retrieved successfully")
                .data(privacyService.getPrivacyList())
                .build();
    }
}
