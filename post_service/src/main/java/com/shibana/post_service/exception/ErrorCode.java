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

    FORBIDDEN(4030, "You do not have permission", HttpStatus.FORBIDDEN),
    FORBIDDEN_OPERATION(4031, "Bạn không có quyền thực hiện", HttpStatus.FORBIDDEN),

    INTERNAL_SERVER_ERROR(5000, "An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ERROR_CODE(5001, "Invalid error code", HttpStatus.INTERNAL_SERVER_ERROR),

    POST_NOT_FOUND(4040, "Post not found", HttpStatus.NOT_FOUND),
    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
