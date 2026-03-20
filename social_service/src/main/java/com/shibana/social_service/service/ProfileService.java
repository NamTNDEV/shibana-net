package com.shibana.social_service.service;

import com.shibana.social_service.dto.RelationshipContext;
import com.shibana.social_service.dto.ViewerContext;
import com.shibana.social_service.dto.request.AvatarUpdateRequest;
import com.shibana.social_service.dto.request.CoverUpdateRequest;
import com.shibana.social_service.dto.request.ProfileCreationRequest;
import com.shibana.social_service.dto.request.ProfileUpdateRequest;
import com.shibana.social_service.dto.response.ProfileMetadataResponse;
import com.shibana.social_service.dto.response.ProfileResponse;
import com.shibana.social_service.dto.response.ProfileDetailResponse;
import com.shibana.social_service.entity.Profile;
import com.shibana.social_service.enums.ProfileField;
import com.shibana.social_service.exception.AppException;
import com.shibana.social_service.exception.ErrorCode;
import com.shibana.social_service.mapper.ProfileMapper;
import com.shibana.social_service.repo.neo4j.ProfileRepo;
import com.shibana.social_service.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    ConnectionsService connectionsService;

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
        var fpList = fieldPrivacyService.getListByUserId(targetProfile.getUserId());
        var currentViewerId = SecurityUtils.getCurrentUserId();
        boolean isOwner = targetProfile.getUserId().equals(currentViewerId);

        boolean isFriended = false;
        boolean isFollowing = false;
        boolean hasSentFriendRequest = false;

        if (!isOwner) {
            var connectionStatuses = connectionsService.getConnectStatuses(currentViewerId, targetProfile.getUserId());
            isFriended = connectionStatuses.isFriended();
            isFollowing = connectionStatuses.isFollowing();
            hasSentFriendRequest = connectionStatuses.hasSentFriendRequest();
        }

        RelationshipContext relationshipContext = new RelationshipContext(isFriended, isFollowing, hasSentFriendRequest);
        ViewerContext viewerContext = new ViewerContext(isOwner, relationshipContext);

        return profileMapper.toProfileDetailResponse(targetProfile, fpList, viewerContext);
    }

    @Transactional("neo4jTransactionManager")
    public ProfileResponse createProfile(ProfileCreationRequest request) {
        Profile profileRequest = profileMapper.toProfileEntity(request);
        Profile savedProfile = profileRepo.save(profileRequest);
        try {
            privacyService.initDefaultFieldsPrivacyForProfile(savedProfile.getUserId());
        } catch (Exception e) {
            log.error("Failed to init privacy for profile {}, rolling back Neo4j", savedProfile.getUserId());
            profileRepo.delete(savedProfile);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return profileMapper.toProfileResponse(savedProfile);
    }

    public ProfileMetadataResponse getMetadataByUserId(String userId) {
        return profileMapper.toProfileMetadataResponse(
                findProfileMetadata(userId)
        );
    }

    @Transactional("neo4jTransactionManager")
    public void updateProfileFieldWithPrivacy(ProfileUpdateRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        Profile targetProfile = findByUserId(userId);
        ProfileField fieldKey = request.getFieldKey();
        String newContent = (request.getContent() == null || request.getContent().isBlank()) ? null : request.getContent();
        boolean isModified = request.getFieldKey().handleUpdate(targetProfile, newContent);

        // Level 0: Chưa tối ưu
        if (isModified) profileRepo.save(targetProfile);

        fieldPrivacyService.updateByUserId(targetProfile.getUserId(), request.getPrivacyLevel(), fieldKey);
    }

    @Transactional("neo4jTransactionManager")
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

    @Transactional("neo4jTransactionManager")
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

}
