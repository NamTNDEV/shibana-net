package com.shibana.media_service.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    FILE_TOO_LARGE(4000103, "media-service:: File size exceeds the limit", HttpStatus.BAD_REQUEST),
    METADATA_INVALID(4000203, "media-service:: Invalid metadata", HttpStatus.BAD_REQUEST),
    MISSING_FILE_PART(4000303, "media-service:: Missing file part in the request", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(4010103, "media-service:: Unauthenticated", HttpStatus.UNAUTHORIZED),

    FORBIDDEN(4030103, "media-service:: You do not have permission", HttpStatus.FORBIDDEN),

    FILE_NOT_FOUND(4040103, "media-service:: File not found", HttpStatus.NOT_FOUND),
    RESOURCE_NOT_FOUND(4040203, "media-service:: Resource not found", HttpStatus.NOT_FOUND),

    UNSUPPORTED_MEDIA_TYPE(4150103, "media-service:: Unsupported file type", HttpStatus.UNSUPPORTED_MEDIA_TYPE),

    INTERNAL_SERVER_ERROR(5000103, "media-service:: An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_UPLOAD_FAILED(5000203, "media-service:: Failed to upload file", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ERROR_CODE(5000303, "media-service:: Invalid error code", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
