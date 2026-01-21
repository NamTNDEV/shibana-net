package com.shibana.api_gateway.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNAUTHENTICATED(4010, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_AUTH_HEADER(4011, "Invalid Authorization header", HttpStatus.UNAUTHORIZED),

    UNKNOWN_ERROR(5000, "An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),

    DOWNSTREAM_ERROR(5020, "Downstream service error", HttpStatus.BAD_GATEWAY),

    DOWNSTREAM_TIMEOUT(5040, "Downstream timeout", HttpStatus.GATEWAY_TIMEOUT),

    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
