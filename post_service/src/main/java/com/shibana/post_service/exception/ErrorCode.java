package com.shibana.post_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNAUTHENTICATED(4010104, "post-service:: Unauthenticated", HttpStatus.UNAUTHORIZED),

    FORBIDDEN(4030104, "post-service:: You do not have permission", HttpStatus.FORBIDDEN),
    FORBIDDEN_OPERATION(4030204, "post-service:: You do not have permission to perform this operation", HttpStatus.FORBIDDEN),

    POST_NOT_FOUND(4040104, "post-service:: Post not found", HttpStatus.NOT_FOUND),

    INTERNAL_SERVER_ERROR(5000104, "post-service:: An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ERROR_CODE(5000204, "post-service:: Invalid error code", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
