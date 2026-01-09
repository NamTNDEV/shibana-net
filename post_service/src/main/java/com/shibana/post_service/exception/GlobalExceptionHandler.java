package com.shibana.post_service.exception;

import com.shibana.post_service.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(
                ApiResponse.<ErrorCode>builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handleRuntimeException(RuntimeException exception) {
        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;
        return ResponseEntity.status(errorCode.getHttpStatus()).body(
                ApiResponse.<ErrorCode>builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

//    @ExceptionHandler(value = AccessDeniedException.class)
//    public ResponseEntity<ApiResponse<ErrorCode>> handleAccessDeniedException(AccessDeniedException exception) {
//        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
//        return ResponseEntity.status(errorCode.getHttpStatus()).body(
//                ApiResponse.<ErrorCode>builder()
//                        .code(errorCode.getCode())
//                        .message(errorCode.getMessage())
//                        .build()
//        );
//    }
}
