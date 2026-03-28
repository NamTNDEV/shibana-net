package com.shibana.post_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    CONTENT_REQUIRED(4000104, "post-service:: Bad request", HttpStatus.BAD_REQUEST),
    INVALID_CONTENT_LENGTH(4000204, "post-service:: Content length is not valid", HttpStatus.BAD_REQUEST),
    INVALID_PRIVACY(4000304, "post-service:: Privacy is not valid", HttpStatus.BAD_REQUEST),
    INVALID_DATA_FORMAT_OR_ENUM_VALUE(4000404, "post-service:: Invalid data format or enum value", HttpStatus.BAD_REQUEST),
    POST_ID_REQUIRED(4000504, "post-service:: Post id is required", HttpStatus.BAD_REQUEST),
    INVALID_CONTENT_LENGHT(4000604, "post-service:: Content length must be between 1 and 300 characters", HttpStatus.BAD_REQUEST),
    COMMENT_DELETE_FAILED(4000704, "post-service:: Failed to delete comment", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(4010104, "post-service:: Unauthenticated", HttpStatus.UNAUTHORIZED),

    FORBIDDEN(4030104, "post-service:: You do not have permission", HttpStatus.FORBIDDEN),
    FORBIDDEN_OPERATION(4030204, "post-service:: You do not have permission to perform this operation", HttpStatus.FORBIDDEN),
    POST_ACCESS_DENIED(4030304, "post-service:: You do not have permission to access this post", HttpStatus.FORBIDDEN),
    POST_UPDATE_DENIED(4030404, "post-service:: You do not have permission to update this post", HttpStatus.FORBIDDEN),
    POST_DELETE_DENIED(4030504, "post-service:: You do not have permission to delete this post", HttpStatus.FORBIDDEN),
    COMMENT_UPDATE_DENIED(4030604, "post-service:: You do not have permission to update this comment", HttpStatus.FORBIDDEN),
    COMMENT_DELETE_DENIED(4030704, "post-service:: You do not have permission to delete this comment", HttpStatus.FORBIDDEN),

    POST_NOT_FOUND(4040104, "post-service:: Post not found", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND(4040204, "post-service:: Comment not found", HttpStatus.NOT_FOUND),

    INTERNAL_SERVER_ERROR(5000104, "post-service:: An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ERROR_CODE(5000204, "post-service:: Invalid error code", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
