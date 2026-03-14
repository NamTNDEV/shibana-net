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

    UNAUTHENTICATED(4010105, "profile-service:: Unauthenticated", HttpStatus.UNAUTHORIZED),

    FORBIDDEN(4030105, "profile-service:: You do not have permission", HttpStatus.FORBIDDEN),

    PROFILE_NOT_FOUND(4040105, "profile-service:: Profile not found", HttpStatus.NOT_FOUND),
    FIELD_PRIVACY_NOT_FOUND(4040106, "profile-service:: Field not found", HttpStatus.NOT_FOUND),
    PRIVACY_NOT_FOUND(4040107, "profile-service:: Privacy level not found", HttpStatus.NOT_FOUND),

    INTERNAL_SERVER_ERROR(5000105, "profile-service:: An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ERROR_CODE(5000106, "profile-service:: Invalid error code", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
