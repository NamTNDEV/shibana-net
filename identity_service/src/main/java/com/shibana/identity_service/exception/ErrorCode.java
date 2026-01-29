package com.shibana.identity_service.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    MALFORMED_TOKEN(4000, "Token không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(4001, "Email không đúng định dạng", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(4002, "Vui lòng nhập Email", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(4003, "Vui lòng nhập mật khẩu", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(4004, "Mật khẩu phải có ít nhất 6 ký tự", HttpStatus.BAD_REQUEST),
    INCORRECT_PASSWORD(4005, "Mật khẩu không chính xác", HttpStatus.BAD_REQUEST),
    INCORRECT_CREDENTIALS(4006, "Email hoặc mật khẩu không đúng", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(4010, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_AUTH_HEADER(4011, "Invalid Authorization header", HttpStatus.UNAUTHORIZED),
    INVALID_SIGNATURE(4012, "Invalid token signature", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(4013, "Phiên đăng nhập hết hạn", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_ISSUER(4014, "Invalid token issuer", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALIDATED(4015, "Token has been invalidated", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_TYPE(4016, "Invalid token type", HttpStatus.UNAUTHORIZED),

    FORBIDDEN(4030, "You do not have permission", HttpStatus.FORBIDDEN),

    USER_EXISTED(4090, "Email này đã được sử dụng", HttpStatus.CONFLICT),
    ROLE_ALREADY_EXISTS(4091, "Role already exists", HttpStatus.CONFLICT),
    PERMISSION_ALREADY_EXISTS(4092, "Permission already exists", HttpStatus.CONFLICT),

    USER_NOT_FOUND(4040, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND(4041, "Permission not found", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(4042, "Role not found", HttpStatus.NOT_FOUND),

    INTERNAL_SERVER_ERROR(5000, "An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ERROR_CODE(5001, "Invalid error code", HttpStatus.INTERNAL_SERVER_ERROR),

    ;

    int code;
    String message;
    HttpStatus httpStatus;
}
