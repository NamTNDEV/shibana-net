package com.shibana.post_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    POST_NOT_FOUND(1001, "Post not found", HttpStatus.NOT_FOUND),
    INVALID_POST_DATA(1003, "Invalid post data provided", HttpStatus.BAD_REQUEST),
    UNKNOWN_ERROR(5000, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(4001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(4003, "Forbidden", HttpStatus.FORBIDDEN),
    FORBIDDEN_OPERATION(4004, "You do not have permission to perform this operation", HttpStatus.FORBIDDEN);

    int code;
    String message;
    HttpStatus httpStatus;
}
