package com.shibana.social_service.service;

import com.shibana.social_service.dto.RelationshipContext;
import com.shibana.social_service.dto.ViewerContext;
import com.shibana.social_service.dto.request.AvatarUpdateRequest;
import com.shibana.social_service.dto.request.CoverUpdateRequest;
import com.shibana.social_service.dto.request.ProfileCreationRequest;
import com.shibana.social_service.dto.request.ProfileUpdateRequest;
import com.shibana.social_service.dto.response.AuthorProfileResponse;
import com.shibana.social_service.dto.response.ProfileMetadataResponse;
import com.shibana.social_service.dto.response.ProfileResponse;
import com.shibana.social_service.dto.response.ProfileDetailResponse;
import com.shibana.social_service.entity.Profile;
import com.shibana.social_service.enums.friendship_status.FriendshipStatus;
import com.shibana.social_service.exception.AppException;
import com.shibana.social_service.exception.ErrorCode;
import com.shibana.social_service.mapper.ProfileMapper;
import com.shibana.social_service.message.dto.payloads.AvatarUpdatedPayload;
import com.shibana.social_service.message.event.AvatarUpdatedLocalEvent;
import com.shibana.social_service.repo.ProfileRepo;
import com.shibana.social_service.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional(readOnly = true)
public class ProfileService {
    ProfileRepo profileRepo;
    ProfileMapper profileMapper;
    ConnectionsService connectionsService;
    ApplicationEventPublisher eventPublisher;

    private Profile findByUserId(UUID userId) {
        return profileRepo.findByUserId(userId).orElseThrow(
                () -> new AppException(ErrorCode.PROFILE_NOT_FOUND)
        );
    }

    private Profile findProfileMetadata(UUID userId) {
        return profileRepo.findProfileMetadata(userId).orElseThrow(
                () -> new AppException(ErrorCode.PROFILE_NOT_FOUND)
        );
    }

    public ProfileDetailResponse getProfileByUsername(String username) {
        var currentViewerId = SecurityUtils.getCurrentUserId();

        Profile targetProfile = profileRepo.findByUsername(username, currentViewerId).orElseThrow(
                () -> new AppException(ErrorCode.PROFILE_NOT_FOUND)
        );

        boolean isOwner = targetProfile.getUserId().equals(currentViewerId);

        FriendshipStatus friendshipStatus = FriendshipStatus.NONE;
        boolean isFollowing = false;

        if (!isOwner) {
            var connectionStatuses = connectionsService.getConnectStatuses(currentViewerId, targetProfile.getUserId());
            friendshipStatus = connectionStatuses.friendshipStatus();
            isFollowing = connectionStatuses.isFollowing();
        }

        RelationshipContext relationshipContext = new RelationshipContext(isFollowing, friendshipStatus);
        ViewerContext viewerContext = new ViewerContext(isOwner, relationshipContext);

        return profileMapper.toProfileDetailResponse(targetProfile, viewerContext);
    }

    @Transactional
    public ProfileResponse createProfile(ProfileCreationRequest request) {
        Profile profileRequest = profileMapper.toProfileEntity(request);
        Profile savedProfile = profileRepo.save(profileRequest);
        return profileMapper.toProfileResponse(savedProfile);
    }

    public ProfileMetadataResponse getMetadataByUserId(UUID userId) {
        return profileMapper.toProfileMetadataResponse(
                findProfileMetadata(userId)
        );
    }

    @Transactional
    public void updateProfileFieldWithPrivacy(ProfileUpdateRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Profile targetProfile = findByUserId(userId);

        String newContent = (request.getContent() == null || request.getContent().isBlank()) ? null : request.getContent();

        boolean isModified = request.getFieldKey().handleUpdate(targetProfile, newContent, request.getPrivacyLevel());

        if (isModified) profileRepo.save(targetProfile);
    }

    @Transactional
    public void updateAvatar(UUID userId, AvatarUpdateRequest request) {
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

        AvatarUpdatedPayload avatarUpdatedPayload = AvatarUpdatedPayload.builder()
                .userId(userId)
                .avatarMediaName(newAvatarName)
                .avatarPositionX(request.getAvatarPositionX())
                .avatarPositionY(request.getAvatarPositionY())
                .avatarScale(request.getAvatarScale())
                .build();

        eventPublisher.publishEvent(new AvatarUpdatedLocalEvent(avatarUpdatedPayload));
    }

    @Transactional
    public void updateCover(UUID userId, CoverUpdateRequest request) {
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

    @Transactional
    public AuthorProfileResponse getAuthorProfileByUserId(UUID userId) {
        Profile profile = findByUserId(userId);
        return profileMapper.toAuthorProfileResponse(profile);
    }
}
