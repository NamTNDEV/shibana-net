package com.shibana.social_service.service;

import com.shibana.social_service.exception.AppException;
import com.shibana.social_service.exception.ErrorCode;
import com.shibana.social_service.repo.neo4j.ConnectionRepo;
import com.shibana.social_service.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class FollowService {
    ConnectionRepo  connectionRepo;

    @Transactional("neo4jTransactionManager")
    public void follow(String followeeId) {
        String followerId = SecurityUtils.getCurrentUserId();
        log.info("The user:: {} follows the followee:: {}",  followerId, followeeId);

        if (followeeId.equals(followerId)) {
            throw new AppException(ErrorCode.CANNOT_FOLLOW_YOUSEFT);
        }

        boolean isSuccess = connectionRepo.follow(followerId, followeeId);
        if (!isSuccess) {
            log.error("Follow failed. Target user {} not found for follower {}", followeeId, followerId);
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
    }

    @Transactional("neo4jTransactionManager")
    public void unfollow(String followeeId) {
        String followerId = SecurityUtils.getCurrentUserId();
        log.info("The user:: {} unfollows the followee:: {}",  followerId, followeeId);
        if (followeeId.equals(followerId)) return;
        connectionRepo.unfollow(followerId, followeeId);
    }

    public boolean checkIsFollowing(String followeeId) {
        String followerId = SecurityUtils.getCurrentUserId();
        if (followeeId.equals(followerId)) return false;
        return connectionRepo.checkIsFollowing(followerId, followeeId);
    }
}
