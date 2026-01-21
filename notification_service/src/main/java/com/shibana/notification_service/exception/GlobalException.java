package com.shibana.notification_service.exception;

import com.shibana.notification_service.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handleAppException(AppException ex) {
        log.error("Application exception occurred:: {}", ex.getMessage());
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(
                        ApiResponse.<ErrorCode>builder()
                                .code(errorCode.getCode())
                                .message(errorCode.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception occurred:: {}", ex.getMessage());
        ErrorCode unknownError = ErrorCode.UNKNOWN_ERROR;
        return ResponseEntity
                .status(unknownError.getHttpStatus())
                .body(
                        ApiResponse.<ErrorCode>builder()
                                .code(unknownError.getCode())
                                .message(unknownError.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public  ResponseEntity<ApiResponse<ErrorCode>> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied exception occurred:: {}", ex.getMessage());
        ErrorCode accessDeniedError = ErrorCode.UNAUTHORIZED;
        return ResponseEntity
                .status(accessDeniedError.getHttpStatus())
                .body(
                        ApiResponse.<ErrorCode>builder()
                                .code(accessDeniedError.getCode())
                                .message(accessDeniedError.getMessage())
                                .build()
                );
    }
}
