package com.shibana.social_service.controller.public_controller;

import com.shibana.social_service.dto.request.AvatarUpdateRequest;
import com.shibana.social_service.dto.request.CoverUpdateRequest;
import com.shibana.social_service.dto.request.ProfileUpdateRequest;
import com.shibana.social_service.dto.response.ApiResponse;
import com.shibana.social_service.dto.response.ProfileResponse;
import com.shibana.social_service.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/profiles")
public class ProfileController {
    ProfileService profileService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/")
    public ApiResponse<List<ProfileResponse>> getAll() {
        return ApiResponse.<List<ProfileResponse>>builder()
                .code(200)
                .message("Profiles retrieved successfully")
                .data(profileService.getAll())
                .build();
    }

    @GetMapping("/{username}")
    public ApiResponse<ProfileResponse> getProfileByUsername(@PathVariable String username) {
        return ApiResponse.<ProfileResponse>builder()
                .code(200)
                .message("Profile retrieved successfully")
                .data(profileService.getProfileByUsername(username))
                .build();
    }

    @PatchMapping("/me/avatar")
    public ApiResponse<ProfileResponse> updateAvatar(
            @RequestBody AvatarUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getClaim("user_id");
        return ApiResponse.<ProfileResponse>builder()
                .code(200)
                .message("Avatar updated successfully")
//                .data(profileService.updateAvatar(userId, request))
                .data(null)
                .build();
    }

    @PatchMapping("/me/cover")
    public ApiResponse<Void> updateCover(
            @Validated @RequestBody CoverUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getClaim("user_id");
        profileService.updateCover(userId, request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Cover updated successfully")
                .build();
    }

}
