package com.shibana.social_service.controller.internal_controller;

import com.shibana.social_service.dto.request.ProfileCreationRequest;
import com.shibana.social_service.dto.response.ApiResponse;
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
@RequestMapping("/internal/profile")
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

//    @GetMapping("/{id}")
//    public ApiResponse<ProfileResponse> getProfile(@PathVariable String id) {
//        return ApiResponse.<ProfileResponse>builder()
//                .code(200)
//                .message("Profile retrieved successfully")
//                .data(profileService.getProfileById(id))
//                .build();
//    }

    @GetMapping("/{userId}")
    public ApiResponse<ProfileResponse> getProfileByUserId(@PathVariable String userId) {
        return ApiResponse.<ProfileResponse>builder()
                .code(200)
                .message("Profile retrieved successfully")
                .data(profileService.getInfo(userId))
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
