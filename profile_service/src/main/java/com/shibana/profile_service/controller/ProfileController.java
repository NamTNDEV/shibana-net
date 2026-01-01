package com.shibana.profile_service.controller;

import com.shibana.profile_service.dto.request.ProfileCreationRequest;
import com.shibana.profile_service.dto.response.ProfileResponse;
import com.shibana.profile_service.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class ProfileController {
    ProfileService profileService;

    @GetMapping("/{id}")
    public ProfileResponse getProfile(@PathVariable String id) {
        return profileService.getProfile(id);
    }

    @PostMapping("/")
    public ProfileResponse createProfile(@RequestBody ProfileCreationRequest request) {
        return profileService.createProfile(request);
    }

    @DeleteMapping("/{id}")
    public String deleteProfile(@PathVariable String id) {
        profileService.deleteProfile(id);
        return "Profile has been deleted";
    }
}
