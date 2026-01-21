package com.shibana.identity_service.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    MALFORMED_TOKEN(4000, "Malformed or unreadable token", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(4010, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(4011, "Invalid token", HttpStatus.UNAUTHORIZED),
    INVALID_SIGNATURE(4012, "Invalid token signature", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(4013, "Token has expired", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_ISSUER(4014, "Invalid token issuer", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALIDATED(4015, "Token has been invalidated", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_TYPE(4016, "Invalid token type", HttpStatus.UNAUTHORIZED),
    INVALID_AUTH_HEADER(4017, "Invalid Authorization header", HttpStatus.UNAUTHORIZED),

    UNAUTHORIZED(4030, "Unauthorized", HttpStatus.FORBIDDEN),

    USER_EXISTED(4090, "Username already exists", HttpStatus.CONFLICT),
    ROLE_ALREADY_EXISTS(4091, "Role already exists", HttpStatus.CONFLICT),
    PERMISSION_ALREADY_EXISTS(4092, "Permission already exists", HttpStatus.CONFLICT),

    USER_NOT_FOUND(4030, "User not found", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND(4031, "Permission not found", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(4032, "Role not found", HttpStatus.NOT_FOUND),

    UNKNOWN_ERROR(5000, "An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ERROR_CODE(5001, "Invalid error code", HttpStatus.INTERNAL_SERVER_ERROR),

    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
