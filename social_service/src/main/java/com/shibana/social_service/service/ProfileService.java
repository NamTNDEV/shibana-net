package com.shibana.social_service.service;

import com.shibana.social_service.dto.PrivacyContext;
import com.shibana.social_service.dto.request.AvatarUpdateRequest;
import com.shibana.social_service.dto.request.CoverUpdateRequest;
import com.shibana.social_service.dto.request.ProfileCreationRequest;
import com.shibana.social_service.dto.request.ProfileUpdateRequest;
import com.shibana.social_service.dto.response.ProfileMetadataResponse;
import com.shibana.social_service.dto.response.ProfileResponse;
import com.shibana.social_service.dto.response.ProfileDetailResponse;
import com.shibana.social_service.entity.Profile;
import com.shibana.social_service.exception.AppException;
import com.shibana.social_service.exception.ErrorCode;
import com.shibana.social_service.mapper.ProfileMapper;
import com.shibana.social_service.repo.neo4j.ProfileRepo;
import com.shibana.social_service.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional(readOnly = true)
public class ProfileService {
    ProfileRepo profileRepo;
    ProfileMapper profileMapper;
    PrivacyService privacyService;
    FieldPrivacyService fieldPrivacyService;

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

    public ProfileDetailResponse getProfileByUsername(String username) {
        Profile targetProfile = profileRepo.findByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.PROFILE_NOT_FOUND)
        );
        var fpList = fieldPrivacyService.getListByProfileId(targetProfile.getId());
        var currentViewerId = SecurityUtils.getCurrentUserId();
        boolean isOwner = targetProfile.getUserId().equals(currentViewerId);
        boolean isFriend = false;
        PrivacyContext privacyContext = new PrivacyContext(isOwner, isFriend);
        return profileMapper.toProfileDetailResponse(targetProfile, fpList, privacyContext);
    }

    @Transactional("neo4jTransactionManager")
    public ProfileResponse createProfile(ProfileCreationRequest request) {
        Profile profileRequest = profileMapper.toProfileEntity(request);
        Profile savedProfile = profileRepo.save(profileRequest);

        try {
            privacyService.initDefaultFieldsPrivacyForProfile(savedProfile.getId());
        } catch (Exception e) {
            log.error("Failed to init privacy for profile {}, rolling back Neo4j", savedProfile.getId());
            profileRepo.delete(savedProfile);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

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

    public void updateAvatar(String userId, AvatarUpdateRequest request) {
        if (request.getAvatarMediaName() == null) {
            throw new AppException(ErrorCode.INVALID_AVATAR);
        }
        Profile existingProfile = findByUserId(userId);
        String oldAvatarName = null;
        String newAvatarName = request.getAvatarMediaName();
        if (!newAvatarName.equals(existingProfile.getAvatarMediaName())) {
            oldAvatarName = existingProfile.getAvatarMediaName();
            existingProfile.setAvatarMediaName(request.getAvatarMediaName());
        }
        existingProfile.setAvatarScale(request.getAvatarScale());
        existingProfile.setAvatarPositionX(request.getAvatarPositionX());
        existingProfile.setAvatarPositionY(request.getAvatarPositionY());
        profileRepo.save(existingProfile);
        if (oldAvatarName != null) {
            log.info("Deleted old avatar media with id {}", oldAvatarName);
        }
    }

    public void updateCover(String userId, CoverUpdateRequest request) {
        Profile existingProfile = findByUserId(userId);
        String oldCoverMediaName = null;
        String newCoverMediaName = request.getCoverMediaName();
        if (newCoverMediaName != null && !newCoverMediaName.equals(existingProfile.getCoverMediaName())) {
            oldCoverMediaName = existingProfile.getCoverMediaName();
            existingProfile.setCoverMediaName(request.getCoverMediaName());
        }
        existingProfile.setCoverPositionY(request.getCoverPositionY());
        profileRepo.save(existingProfile);
        if (oldCoverMediaName != null) {
            log.info("Deleted old cover media with id {}", oldCoverMediaName);
        }
    }

    public void deleteProfile(String id) {
        profileRepo.deleteById(id);
    }
}
