package com.shibana.profile_service.service;

import com.shibana.profile_service.dto.request.AvatarUpdateRequest;
import com.shibana.profile_service.dto.request.ProfileCreationRequest;
import com.shibana.profile_service.dto.request.ProfileUpdateRequest;
import com.shibana.profile_service.dto.response.ProfileResponse;
import com.shibana.profile_service.entity.Profile;
import com.shibana.profile_service.exception.AppException;
import com.shibana.profile_service.exception.ErrorCode;
import com.shibana.profile_service.mapper.ProfileMapper;
import com.shibana.profile_service.repo.ProfileRepo;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProfileService {
    ProfileRepo profileRepo;
    ProfileMapper profileMapper;

    private Profile findProfileByUserId(String userId) {
        return profileRepo.findByUserId(userId).orElseThrow(
                () -> {
                    log.error("Profile with userId {} not found", userId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                }
        );
    }

    public ProfileResponse createProfile(ProfileCreationRequest request) {
        Profile profileRequest = profileMapper.toProfileEntity(request);
        Profile savedProfile = profileRepo.save(profileRequest);
        return profileMapper.toProfileResponse(savedProfile);
    }

    public List<ProfileResponse> getAllProfiles() {
        List<Profile> profiles = profileRepo.findAll();
        return profiles.stream()
                .map(profileMapper::toProfileResponse)
                .toList();
    }

    public ProfileResponse getProfile(String id) {
        Profile profile = profileRepo.findById(id).orElseThrow(
                () -> {
                    log.error("Profile with id {} not found", id);
                    return new RuntimeException("Profile not found");
                }
        );
        return profileMapper.toProfileResponse(profile);
    }

    public ProfileResponse getMe (String userId) {
        Profile profile = findProfileByUserId(userId);
        return profileMapper.toProfileResponse(profile);
    }

    public ProfileResponse updateMe(String userId, ProfileUpdateRequest request) {
        Profile existingProfile = findProfileByUserId(userId);
        profileMapper.updateProfileFromRequest(existingProfile, request);
        Profile updatedProfile = profileRepo.save(existingProfile);
        return profileMapper.toProfileResponse(updatedProfile);
    }

    public ProfileResponse updateAvatar(String userId, AvatarUpdateRequest request) {
        Profile existingProfile = findProfileByUserId(userId);
        existingProfile.setAvatar(request.getAvatarUrl());
        Profile updatedProfile = profileRepo.save(existingProfile);
        return profileMapper.toProfileResponse(updatedProfile);
    }

    public void deleteProfile(String id) {
        profileRepo.deleteById(id);
    }
}
