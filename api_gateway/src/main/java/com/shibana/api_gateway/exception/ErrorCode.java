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
    UNAUTHENTICATED(4010101, "api-gateway:: Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_AUTH_HEADER(4010201, "api-gateway:: Invalid Authorization header", HttpStatus.UNAUTHORIZED),

    INTERNAL_SERVER_ERROR(5000101, "api-gateway:: An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
