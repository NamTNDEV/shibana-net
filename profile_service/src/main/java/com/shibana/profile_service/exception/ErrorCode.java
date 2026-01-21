package com.shibana.profile_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNAUTHENTICATED(4010, "Unauthenticated", HttpStatus.UNAUTHORIZED),

    UNAUTHORIZED(4030, "Forbidden", HttpStatus.FORBIDDEN),

    INVALID_REQUEST(4000, "Invalid request", HttpStatus.BAD_REQUEST),

    UNKNOWN_ERROR(5000, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),

    PROFILE_NOT_FOUND(4040, "Profile not found", HttpStatus.NOT_FOUND),
    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
