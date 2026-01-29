package com.shibana.identity_service.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    MALFORMED_TOKEN(4000, "Malformed or unreadable token", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(4001, "Invalid email format", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(4002, "Email is required", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(4003, "Password is required", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(4004, "Password must be at least 6 characters", HttpStatus.BAD_REQUEST),
    INCORRECT_PASSWORD(4005, "Incorrect password", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(4010, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(4011, "Invalid token", HttpStatus.UNAUTHORIZED),
    INVALID_SIGNATURE(4012, "Invalid token signature", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(4013, "Token has expired", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_ISSUER(4014, "Invalid token issuer", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALIDATED(4015, "Token has been invalidated", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_TYPE(4016, "Invalid token type", HttpStatus.UNAUTHORIZED),
    INVALID_AUTH_HEADER(4017, "Invalid Authorization header", HttpStatus.UNAUTHORIZED),

    UNAUTHORIZED(4030, "Unauthorized", HttpStatus.FORBIDDEN),

    USER_EXISTED(4090, "Email already exists", HttpStatus.CONFLICT),
    ROLE_ALREADY_EXISTS(4091, "Role already exists", HttpStatus.CONFLICT),
    PERMISSION_ALREADY_EXISTS(4092, "Permission already exists", HttpStatus.CONFLICT),

    USER_NOT_FOUND(4040, "User not found", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND(4041, "Permission not found", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(4042, "Role not found", HttpStatus.NOT_FOUND),

    UNKNOWN_ERROR(5000, "An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ERROR_CODE(5001, "Invalid error code", HttpStatus.INTERNAL_SERVER_ERROR),

    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
