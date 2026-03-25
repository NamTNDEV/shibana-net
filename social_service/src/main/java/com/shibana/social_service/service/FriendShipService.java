package com.shibana.social_service.service;

import com.shibana.social_service.enums.friendship_status.FriendRequestEligibilityStatus;
import com.shibana.social_service.enums.friendship_status.FriendResponseEligibilityStatus;
import com.shibana.social_service.exception.AppException;
import com.shibana.social_service.exception.ErrorCode;
import com.shibana.social_service.repo.neo4j.ConnectionRepo;
import com.shibana.social_service.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class FriendShipService {
    ConnectionRepo connectionRepo;

    private void executeFriendResponse(String responderId, String requesterId, Runnable handler) {
        var status = connectionRepo.checkFriendResponseEligibility(responderId, requesterId);
        switch (status) {
            case PROFILE_NOT_FOUND -> throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
            case FRIENDED -> throw new AppException(ErrorCode.ALREADY_FRIENDS);
            case NO_SEND_REQUEST -> throw new AppException(ErrorCode.NO_SEND_REQUEST);
            case READY -> handler.run();
            default -> throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional("neo4jTransactionManager")
    public void sendAddFriendRequest(String recieverId) {
        String senderId = SecurityUtils.getCurrentUserId();
        if (recieverId.equals(senderId)) {
            throw new AppException(ErrorCode.CANNOT_BE_FRIEND_YOURSEFT);
        }
        var status = connectionRepo.checkFriendRequestEligibility(senderId, recieverId);
        switch (status) {
            case PROFILE_NOT_FOUND -> throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
            case BE_BLOCKED -> throw new AppException(ErrorCode.BE_BLOCKED);
            case SENT_REQUEST -> log.warn("User {} has already sent a friend request to user {}", senderId, recieverId);
            case RECEIVED_REQUEST -> {
                log.warn("Cross-request detected! Auto-accepting friendship between {} and {}", senderId, recieverId);
                connectionRepo.acceptFriendRequest(senderId, recieverId);
            }
            case BE_REJECTED -> throw new AppException(ErrorCode.FRIEND_REQUEST_COOLDOWN);
            case FRIENDED -> throw new AppException(ErrorCode.ALREADY_FRIENDS);
            case READY -> connectionRepo.sendFriendRequest(senderId, recieverId);
            default -> throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional("neo4jTransactionManager")
    public void acceptFriendRequest(String requesterId) {
        String acceptorId = SecurityUtils.getCurrentUserId();
        if (requesterId.equals(acceptorId)) {
            throw new AppException(ErrorCode.CANNOT_ACCEPT_YOURSEFT);
        }
        executeFriendResponse(
                acceptorId,
                requesterId,
                () -> connectionRepo.acceptFriendRequest(acceptorId, requesterId)
        );
    }

    @Transactional("neo4jTransactionManager")
    public void rejectFriendRequest(String requesterId) {
        String rejectorId = SecurityUtils.getCurrentUserId();
        if (requesterId.equals(rejectorId)) {
            throw new AppException(ErrorCode.CANNOT_CANCEL_YOURSEFT);
        }
        executeFriendResponse(
                rejectorId,
                requesterId,
                () -> connectionRepo.rejectFriendRequest(rejectorId, requesterId)
        );
    }

    @Transactional("neo4jTransactionManager")
    public void unfriend(String unfriendeeId) {
        String unfrienderId = SecurityUtils.getCurrentUserId();
        if (unfriendeeId.equals(unfrienderId)) {
            throw new AppException(ErrorCode.CANNOT_UNFRIEND_YOURSEFT);
        }
        connectionRepo.unfriend(unfrienderId, unfriendeeId);
    }

    @Transactional("neo4jTransactionManager")
    public void revokeFriendRequest(String revokeeId) {
        String revokerId = SecurityUtils.getCurrentUserId();
        if (revokeeId.equals(revokerId)) {
            throw new AppException(ErrorCode.CANNOT_UNFRIEND_YOURSEFT);
        }
        var status = connectionRepo.checkRevokeFriendResponseEligibility(revokerId, revokeeId);
        switch (status) {
            case PROFILE_NOT_FOUND -> throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
            case NO_SEND_REQUEST -> throw new AppException(ErrorCode.NO_SEND_REQUEST);
            case READY -> connectionRepo.revokeRequest(revokerId, revokeeId);
            default -> throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean checkIsFriend(String viewerId, String authorId) {
        return connectionRepo.checkFriendRequestEligibility(viewerId, authorId) == FriendRequestEligibilityStatus.FRIENDED;
    }
}
