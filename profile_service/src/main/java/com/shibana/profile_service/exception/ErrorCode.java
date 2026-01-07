package com.shibana.profile_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNKNOWN_ERROR(5000, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    PROFILE_NOT_FOUND(4004, "Profile not found", HttpStatus.NOT_FOUND),
    INVALID_REQUEST(4000, "Invalid request", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(4001, "Unauthenticated", HttpStatus.FORBIDDEN),
    UNAUTHORIZED(4003, "Forbidden", HttpStatus.UNAUTHORIZED);


    int code;
    String message;
    HttpStatus httpStatus;
}
