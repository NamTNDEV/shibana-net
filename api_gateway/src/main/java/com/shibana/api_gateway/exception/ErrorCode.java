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
    UNAUTHENTICATED(4101, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_AUTH_HEADER(4207, "Invalid Authorization header", HttpStatus.UNAUTHORIZED),

    DOWNSTREAM_ERROR(4901, "Downstream service error", HttpStatus.BAD_GATEWAY),
    DOWNSTREAM_TIMEOUT(4902, "Downstream timeout", HttpStatus.GATEWAY_TIMEOUT),

    UNKNOWN_ERROR(4999, "An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR);

    int code;
    String message;
    HttpStatus httpStatus;
}
