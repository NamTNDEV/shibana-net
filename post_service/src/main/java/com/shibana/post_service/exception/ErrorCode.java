package com.shibana.post_service.exception;

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
    FORBIDDEN_OPERATION(4031, "You do not have permission to perform this operation", HttpStatus.FORBIDDEN),

    UNKNOWN_ERROR(5000, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ERROR_CODE(5001, "Invalid error code", HttpStatus.INTERNAL_SERVER_ERROR),

    POST_NOT_FOUND(4000, "Post not found", HttpStatus.NOT_FOUND),
    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
