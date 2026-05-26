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
import com.shibana.social_service.message.dto.EventType;
import com.shibana.social_service.message.dto.payloads.AvatarUpdatedPayload;
import com.shibana.social_service.message.outbox.dto.OutboxCreationRequest;
import com.shibana.social_service.message.outbox.enums.AggregateType;
import com.shibana.social_service.message.outbox.service.OutboxService;
import com.shibana.social_service.repo.ProfileRepo;
import com.shibana.social_service.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
    OutboxService outboxService;

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
    public void createProfile(ProfileCreationRequest request) {
        Profile profileRequest = profileMapper.toProfileEntity(request);
        profileRepo.save(profileRequest);
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
        String newAvatarName = request.getAvatarMediaName();
        Instant now = Instant.now();

        existingProfile.setAvatarScale(request.getAvatarScale());
        existingProfile.setAvatarPositionX(request.getAvatarPositionX());
        existingProfile.setAvatarPositionY(request.getAvatarPositionY());
        existingProfile.setUpdatedAt(now);

        profileRepo.save(existingProfile);

        AvatarUpdatedPayload payload = AvatarUpdatedPayload.builder()
                .userId(userId)
                .avatarMediaName(newAvatarName)
                .avatarPositionX(request.getAvatarPositionX())
                .avatarPositionY(request.getAvatarPositionY())
                .avatarScale(request.getAvatarScale())
                .build();

        var outboxCreationRequest = OutboxCreationRequest.builder()
                .aggregateId(userId.toString())
                .aggregateType(AggregateType.PROFILE.name())
                .eventType(EventType.AVATAR_UPDATED)
                .eventPayload(payload)
                .createdAt(now)
                .build();

        outboxService.creatAndPublishOutboxEvent(outboxCreationRequest);
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
