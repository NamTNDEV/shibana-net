package com.shibana.social_service.service;

import com.shibana.social_service.dto.request.AvatarUpdateRequest;
import com.shibana.social_service.dto.request.CoverUpdateRequest;
import com.shibana.social_service.dto.request.ProfileCreationRequest;
import com.shibana.social_service.dto.request.ProfileUpdateRequest;
import com.shibana.social_service.dto.response.ProfileMetadataResponse;
import com.shibana.social_service.dto.response.ProfileResponse;
import com.shibana.social_service.entity.Profile;
import com.shibana.social_service.exception.AppException;
import com.shibana.social_service.exception.ErrorCode;
import com.shibana.social_service.mapper.ProfileMapper;
import com.shibana.social_service.repo.ProfileRepo;
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

    private Profile findByUserId(String userId) {
        return profileRepo.findByUserId(userId).orElseThrow(
                () -> new AppException(ErrorCode.PROFILE_NOT_FOUND)
        );
    }

    private Profile findProfileMetadata(String userId) {
        return profileRepo.findProfileMetadata(userId).orElseThrow(
                () -> new AppException(ErrorCode.PROFILE_NOT_FOUND)
        );
    }

    public ProfileResponse getProfileByUsername(String username) {
        Profile profile = profileRepo.findByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.PROFILE_NOT_FOUND)
        );
        return profileMapper.toProfileResponse(profile);
    }

    public ProfileResponse createProfile(ProfileCreationRequest request) {
        Profile profileRequest = profileMapper.toProfileEntity(request);
        Profile savedProfile = profileRepo.save(profileRequest);
        return profileMapper.toProfileResponse(savedProfile);
    }

    public List<ProfileResponse> getAll() {
        List<Profile> profiles = profileRepo.findAll();
        return profiles.stream()
                .map(profileMapper::toProfileResponse)
                .toList();
    }

    public ProfileResponse getProfileById(String id) {
        Profile profile = profileRepo.findById(id).orElseThrow(
                () -> {
                    log.error("Profile with id {} not found", id);
                    return new RuntimeException("Profile not found");
                }
        );
        return profileMapper.toProfileResponse(profile);
    }

    public ProfileMetadataResponse getMetadataByUserId(String userId) {
        return profileMapper.toProfileMetadataResponse(
                findProfileMetadata(userId)
        );
    }

    public ProfileResponse updateInfo(String userId, ProfileUpdateRequest request) {
        Profile existingProfile = findByUserId(userId);
        profileMapper.updateProfileFromRequest(existingProfile, request);
        Profile updatedProfile = profileRepo.save(existingProfile);
        return profileMapper.toProfileResponse(updatedProfile);
    }

//    public ProfileResponse updateAvatar(String userId, AvatarUpdateRequest request) {
//        Profile existingProfile = findByUserId(userId);
//        existingProfile.setAvatar(request.getAvatarUrl());
//        Profile updatedProfile = profileRepo.save(existingProfile);
//        return profileMapper.toProfileResponse(updatedProfile);
//    }

    public void updateCover(String userId, CoverUpdateRequest request) {
        Profile existingProfile = findByUserId(userId);
        String oldCoverMediaName = null;
        String newCoverMediaId = request.getCoverMediaName();
        if(newCoverMediaId != null && !newCoverMediaId.equals(existingProfile.getCoverMediaName())) {
            oldCoverMediaName = existingProfile.getCoverMediaName();
            existingProfile.setCoverMediaName(request.getCoverMediaName());
        }
        existingProfile.setCoverPositionY(request.getCoverPositionY());
        profileRepo.save(existingProfile);
        if(oldCoverMediaName != null) {
            log.info("Deleted old cover media with id {}", oldCoverMediaName);
        }
    }

    public void deleteProfile(String id) {
        profileRepo.deleteById(id);
    }
}
