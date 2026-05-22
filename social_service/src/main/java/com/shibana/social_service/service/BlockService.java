package com.shibana.social_service.service;

import com.shibana.social_service.enums.block_status.BlockEligibilityStatus;
import com.shibana.social_service.exception.AppException;
import com.shibana.social_service.exception.ErrorCode;
import com.shibana.social_service.repo.ConnectionRepo;
import com.shibana.social_service.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlockService {
    ConnectionRepo connectionRepo;

    @Transactional
    public void blockUser(UUID blockeeId) {
        log.info("Blocking user with ID:: {}", blockeeId);
        UUID blockerId = SecurityUtils.getCurrentUserId();

        if (blockeeId.equals(blockerId)) {
            throw new AppException(ErrorCode.CANNOT_BLOCK_YOURSEFT);
        }

        var eligibilityStatus = connectionRepo.checkBlockingEligibility(blockerId, blockeeId);
        switch (eligibilityStatus) {
            case PROFILE_NOT_FOUND -> throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
            case ALREADY_BLOCKED -> throw new AppException(ErrorCode.ALREADY_BLOCKED);
            case BE_BLOCKED, READY -> {
                if (eligibilityStatus == BlockEligibilityStatus.BE_BLOCKED) {
                    log.warn("This user has been blocked by this blockee");
                }
                connectionRepo.blocks(blockerId, blockeeId);
            }
        }
    }

    public void unblockUser(UUID blockeeId) {
        log.info("Unblocking user with ID:: {}", blockeeId);
        UUID blockerId = SecurityUtils.getCurrentUserId();
        if (blockeeId.equals(SecurityUtils.getCurrentUserId())) {
            throw new AppException(ErrorCode.CANNOT_BLOCK_YOURSEFT);
        }
        connectionRepo.unblocks(blockerId, blockeeId);
    }
}
