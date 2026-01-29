package com.shibana.notification_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public enum ErrorCode {
    UNAUTHENTICATED(4010106, "notification-service:: Unauthenticated", HttpStatus.UNAUTHORIZED),

    FORBIDDEN(4030106, "notification-service:: You do not have permission", HttpStatus.FORBIDDEN),

    INTERNAL_SERVER_ERROR(5000106, "notification-service:: An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    SEND_EMAIL_FAILED(5000206, "notification-service:: Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
