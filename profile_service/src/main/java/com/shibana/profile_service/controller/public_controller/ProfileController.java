package com.shibana.profile_service.controller.public_controller;

import com.shibana.profile_service.dto.response.ApiResponse;
import com.shibana.profile_service.dto.response.ProfileResponse;
import com.shibana.profile_service.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class ProfileController {
    ProfileService profileService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/")
    public ApiResponse<List<ProfileResponse>> getAllProfiles() {
        return ApiResponse.<List<ProfileResponse>>builder()
                .code(200)
                .message("Profiles retrieved successfully")
                .data(profileService.getAllProfiles())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProfileResponse> getProfile(@PathVariable String id) {
        return ApiResponse.<ProfileResponse>builder()
                .code(200)
                .message("Profile retrieved successfully")
                .data(profileService.getProfile(id))
                .build();
    }
}
