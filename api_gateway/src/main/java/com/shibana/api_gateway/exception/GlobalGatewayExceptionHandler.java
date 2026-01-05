package com.shibana.api_gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shibana.api_gateway.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Order(-2)
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalGatewayExceptionHandler implements ErrorWebExceptionHandler {
    ObjectMapper objectMapper;

    private ErrorCode mapExceptionToErrorCode(Throwable ex) {
        if (ex instanceof AppException app) {
            log.warn("Gateway AppException: {}", app.getErrorCode());
            return app.getErrorCode();
        }

        log.error("Gateway unknown exception", ex);
        return ErrorCode.UNKNOWN_ERROR;
    }

    @Override
    @NonNull
    public Mono<Void> handle(
            ServerWebExchange exchange,
            @NonNull Throwable ex
    ) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        ErrorCode errorCode = mapExceptionToErrorCode(ex);
        var response = exchange.getResponse();

        response.setStatusCode(errorCode.getHttpStatus());
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        byte[] bodyBytes;
        try {
            bodyBytes = objectMapper.writeValueAsBytes(body);
        } catch (Exception e) {
            log.error("Error serializing response body", e);
            bodyBytes = ("{\"code\":4999,\"message\":\"An unknown error occurred\"}")
                    .getBytes(StandardCharsets.UTF_8);
        }

        return response.writeWith(
                Mono.just(
                        response.bufferFactory().wrap(bodyBytes)
                )
        );
    }
}
