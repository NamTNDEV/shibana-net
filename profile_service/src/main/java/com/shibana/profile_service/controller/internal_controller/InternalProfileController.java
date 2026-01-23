package com.shibana.profile_service.controller.internal_controller;

import com.shibana.profile_service.dto.request.ProfileCreationRequest;
import com.shibana.profile_service.dto.response.ApiResponse;
import com.shibana.profile_service.dto.response.ProfileResponse;
import com.shibana.profile_service.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RestController()
@RequestMapping("/internal")
public class InternalProfileController {
    ProfileService profileService;

    @PostMapping("/")
    public ApiResponse<ProfileResponse> createProfile(@RequestBody ProfileCreationRequest request) {
        return ApiResponse.<ProfileResponse>builder()
                .code(201)
                .message("Profile created successfully")
                .data(profileService.createProfile(request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProfile(
            @PathVariable String id
    ) {
        profileService.deleteProfile(id);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Profile deleted successfully")
                .data(id)
                .build();
    }
}
