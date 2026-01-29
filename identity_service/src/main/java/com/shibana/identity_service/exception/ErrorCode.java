package com.shibana.identity_service.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    MALFORMED_TOKEN(4000102, "identity-service:: Malformed or unreadable token", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(4000202, "identity-service:: Invalid email format", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(4000302, "identity-service:: Email is required", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(4000402, "identity-service:: Password is required", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(4000502, "identity-service:: Password must be at least 6 characters", HttpStatus.BAD_REQUEST),
    INCORRECT_PASSWORD(4000602, "identity-service:: Incorrect password", HttpStatus.BAD_REQUEST),
    INCORRECT_CREDENTIALS(4000702, "identity-service:: Email or password is incorrect", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(4010102, "identity-service:: Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_AUTH_HEADER(4010202, "identity-service:: Invalid Authorization header", HttpStatus.UNAUTHORIZED),
    INVALID_SIGNATURE(4010302, "identity-service:: Invalid token signature", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(4010402, "identity-service:: Token has expired", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_ISSUER(4010502, "identity-service:: Invalid token issuer", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALIDATED(4010602, "identity-service:: Token has been invalidated", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_TYPE(4010702, "identity-service:: Invalid token type", HttpStatus.UNAUTHORIZED),

    FORBIDDEN(4030102, "identity-service:: You do not have permission", HttpStatus.FORBIDDEN),

    USER_NOT_FOUND(4040102, "identity-service:: User not found", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND(4040202, "identity-service:: Permission not found", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(4040302, "identity-service:: Role not found", HttpStatus.NOT_FOUND),

    USER_EXISTED(4090102, "identity-service:: Email already exists", HttpStatus.CONFLICT),
    ROLE_ALREADY_EXISTS(4090202, "identity-service:: Role already exists", HttpStatus.CONFLICT),
    PERMISSION_ALREADY_EXISTS(4090302, "identity-service:: Permission already exists", HttpStatus.CONFLICT),

    INTERNAL_SERVER_ERROR(5000102, "identity-service:: An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ERROR_CODE(5000202, "identity-service:: Invalid error code", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
