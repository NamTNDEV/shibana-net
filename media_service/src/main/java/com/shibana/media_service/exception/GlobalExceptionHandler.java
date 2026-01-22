package com.shibana.media_service.exception;

import com.shibana.media_service.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Map;
import java.util.Objects;

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

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handleMaxSizeException(MaxUploadSizeExceededException exception) {
        ErrorCode errorCode = ErrorCode.FILE_TOO_LARGE;
        return ResponseEntity.status(errorCode.getHttpStatus()).body(
                ApiResponse.<ErrorCode>builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handleMissingServletRequestPartException(MissingServletRequestPartException exception) {
        ErrorCode errorCode = ErrorCode.MISSING_FILE_PART;
        return ResponseEntity.status(errorCode.getHttpStatus()).body(
                ApiResponse.<ErrorCode>builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse<ErrorCode>> handleAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getHttpStatus()).body(
                ApiResponse.<ErrorCode>builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException exception) {
        ErrorCode errorCode = ErrorCode.INVALID_ERROR_CODE;
        String enumErrorCode = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        try {
            errorCode = ErrorCode.valueOf(enumErrorCode);
        } catch (IllegalArgumentException ignored) {
        }

        ConstraintViolation<?> constraintViolationException = exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
        Map<String, Object> attributes = constraintViolationException.getConstraintDescriptor().getAttributes();
        String newMessage = mappingAttributesToString(errorCode.getMessage(), attributes);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setCode(errorCode.getCode());
        response.setMessage(newMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

//    @ExceptionHandler(value = FeignException.class)
//    public ResponseEntity<ApiResponse<Void>> handleFeignException(FeignException exception) {
//        ApiResponse<Void> response = new ApiResponse<>();
//        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;
//        if (exception.status() == HttpStatus.UNAUTHORIZED.value()) {
//            errorCode = ErrorCode.UNAUTHENTICATED;
//        } else if (exception.status() == HttpStatus.FORBIDDEN.value()) {
//            errorCode = ErrorCode.UNAUTHORIZED;
//        }
//        response.setCode(errorCode.getCode());
//        response.setMessage(errorCode.getMessage());
//        return ResponseEntity.status(exception.status()).body(response);
//    }

    private String mappingAttributesToString(String oldMessage, Map<String, Object> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return oldMessage;
        }

        String newMessage = oldMessage;
        for (var entry : attributes.entrySet()) {
            String key = entry.getKey();
            if (key.equals("message") || key.equals("groups") || key.equals("payload")) {
                continue;
            }
            String value = entry.getValue().toString();
            newMessage = newMessage.replace("{" + key + "}", value);
        }
        return newMessage;
    }
}
