package com.shibana.social_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    INVALID_BIO_LENGTH(4000105, "profile-service:: Bio length is not valid", HttpStatus.BAD_REQUEST),
    INVALID_COVER_POSITION(4000106, "profile-service:: Cover position must be between 0 and 100", HttpStatus.BAD_REQUEST),
    COVER_POSITION_Y_REQUIRED(4000107, "profile-service:: Cover position Y is required", HttpStatus.BAD_REQUEST),
    INVALID_AVATAR(4000108, "profile-service:: Invalid avatar (Avatar must be an image file)", HttpStatus.BAD_REQUEST),
    INVALID_UPDATE_PRIVACY_LEVEL(4000109, "profile-service:: Invalid update privacy level", HttpStatus.BAD_REQUEST),
    INVALID_UPDATE_PROFILE_FIELD(4000110, "profile-service:: Invalid update profile field", HttpStatus.BAD_REQUEST),
    INVALID_FOLLOW_BODY(4000111, "profile-service:: FolloweeId is not valid", HttpStatus.BAD_REQUEST),
    CANNOT_FOLLOW_YOUSEFT(4000112, "profile-service:: Cannot follow yourself", HttpStatus.BAD_REQUEST),
    CANNOT_BE_FRIEND_YOURSEFT(4000113, "profile-service:: Cannot be friend with yourself", HttpStatus.BAD_REQUEST),
    CANNOT_ACCEPT_YOURSEFT(4000114, "profile-service:: Cannot accept yourself", HttpStatus.BAD_REQUEST),
    ALREADY_FRIENDS(4000114, "profile-service:: You are already friends with this user", HttpStatus.BAD_REQUEST),
    NO_SEND_REQUEST(4000115, "profile-service:: There is not request to accept", HttpStatus.BAD_REQUEST),
    CANNOT_CANCEL_YOURSEFT(4000116, "profile-service:: Cannot cancel friend request to yourself", HttpStatus.BAD_REQUEST),
    CANNOT_UNFRIEND_YOURSEFT(4000117, "profile-service:: Cannot unfriend yourself", HttpStatus.BAD_REQUEST),
    NOT_FRIENDS(4000118, "profile-service:: You are not friends with this user", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_COOLDOWN(4000119, "profile-service:: You must wait before sending another friend request to this user in 30 days", HttpStatus.BAD_REQUEST),
    CANNOT_BLOCK_YOURSEFT(40001120, "profile-service:: Cannot block yourself", HttpStatus.BAD_REQUEST),
    ALREADY_BLOCKED(40001121, "profile-service:: You have already blocked this user", HttpStatus.BAD_REQUEST),
    BE_BLOCKED(40001122, "profile-service:: You have been blocked by this user", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(4010105, "profile-service:: Unauthenticated", HttpStatus.UNAUTHORIZED),

    FORBIDDEN(4030105, "profile-service:: You do not have permission", HttpStatus.FORBIDDEN),

    PROFILE_NOT_FOUND(4040105, "profile-service:: Profile not found", HttpStatus.NOT_FOUND),
    FIELD_PRIVACY_NOT_FOUND(4040106, "profile-service:: Field not found", HttpStatus.NOT_FOUND),
    PRIVACY_NOT_FOUND(4040107, "profile-service:: Privacy level not found", HttpStatus.NOT_FOUND),

    INTERNAL_SERVER_ERROR(5000105, "profile-service:: An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ERROR_CODE(5000106, "profile-service:: Invalid error code", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_JSON_PARSING(5000107, "profile-service:: Invalid JSON parsing", HttpStatus.INTERNAL_SERVER_ERROR),
    SERIALIZATION_ERROR(5000108, "profile-service:: Serialization error", HttpStatus.INTERNAL_SERVER_ERROR)
    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
