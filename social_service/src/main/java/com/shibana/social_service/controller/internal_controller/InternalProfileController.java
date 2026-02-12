package com.shibana.social_service.controller.internal_controller;

import com.shibana.social_service.dto.request.ProfileCreationRequest;
import com.shibana.social_service.dto.response.ApiResponse;
import com.shibana.social_service.dto.response.ProfileMetadataResponse;
import com.shibana.social_service.dto.response.ProfileResponse;
import com.shibana.social_service.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RestController()
@RequestMapping("/internal/profiles")
public class InternalProfileController {
    ProfileService profileService;

    @PostMapping("/")
    public ApiResponse<ProfileResponse> provisionProfile(@RequestBody ProfileCreationRequest request) {
        return ApiResponse.<ProfileResponse>builder()
                .code(201)
                .message("Profile created successfully")
                .data(profileService.createProfile(request))
                .build();
    }

    @GetMapping("/{userId}/metadata")
    public ApiResponse<ProfileMetadataResponse> getInternalMetadata(@PathVariable String userId) {
        return ApiResponse.<ProfileMetadataResponse>builder()
                .code(200)
                .message("Profile retrieved successfully")
                .data(profileService.getMetadataByUserId(userId))
                .build();
    }
}
