package com.shibana.notification_service.exeption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public enum ErrorCode {
    UNKNOWN_ERROR(5000, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(4001, "Unauthenticated", HttpStatus.FORBIDDEN),
    UNAUTHORIZED(4003, "Forbidden", HttpStatus.UNAUTHORIZED),
    SEND_EMAIL_FAILED(5001, "Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR);

    int code;
    String message;
    HttpStatus httpStatus;
}
