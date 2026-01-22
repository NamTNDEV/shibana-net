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
    UNAUTHENTICATED(4010, "Unauthenticated", HttpStatus.UNAUTHORIZED),

    UNAUTHORIZED(4030, "Forbidden", HttpStatus.FORBIDDEN),

    METADATA_INVALID(4000, "Invalid metadata", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(4001, "File size exceeds the limit", HttpStatus.BAD_REQUEST),
    MISSING_FILE_PART(4002, "Missing file part in the request", HttpStatus.BAD_REQUEST),

    FILE_NOT_FOUND(4040, "File not found", HttpStatus.NOT_FOUND),

    UNSUPPORTED_MEDIA_TYPE(4150, "Unsupported file type", HttpStatus.UNSUPPORTED_MEDIA_TYPE),

    UNKNOWN_ERROR(5000, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_UPLOAD_FAILED(5001, "Failed to upload file", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ERROR_CODE(5002, "Invalid error code", HttpStatus.INTERNAL_SERVER_ERROR)
    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
