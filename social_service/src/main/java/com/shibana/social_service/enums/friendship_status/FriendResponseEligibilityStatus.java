package com.shibana.social_service.enums.friendship_status;

import com.shibana.social_service.exception.AppException;
import com.shibana.social_service.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.BiConsumer;

@Slf4j
public enum FriendResponseEligibilityStatus {
    PROFILE_NOT_FOUND,
    NO_SEND_REQUEST,
    FRIENDED,
    READY
}
