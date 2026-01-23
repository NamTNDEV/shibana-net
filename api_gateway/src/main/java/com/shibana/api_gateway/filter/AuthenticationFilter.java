package com.shibana.api_gateway.filter;

import com.shibana.api_gateway.exception.AppException;
import com.shibana.api_gateway.exception.ErrorCode;
import com.shibana.api_gateway.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {
    AuthService authService;

    @Value("${server.api-prefix}")
    @NonFinal
    String apiPrefix;

    String[] excludedPaths = {
            "/identity/auth/login",
            "/identity/auth/register",
            "/media/static/.*",
            "/profile/me",
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();
        var path = request.getURI().getPath();
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }
        if (isMatchedExcludedPath(path)) {
            return chain.filter(exchange);
        }
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isBlank()) {
            return Mono.error(new AppException(ErrorCode.UNAUTHENTICATED));
        }
        if (!authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return Mono.error(new AppException(ErrorCode.INVALID_AUTH_HEADER));
        }
        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            return Mono.error(new AppException(ErrorCode.INVALID_AUTH_HEADER));
        }
        return authService.introspectToken(token).flatMap(response -> {
            if (response != null && response.getData() != null && response.getData().isValid()) {
                return chain.filter(exchange);
            } else {
                return Mono.error(new AppException(ErrorCode.UNAUTHENTICATED));
            }
        }).onErrorResume(error -> Mono.error(new AppException(ErrorCode.UNAUTHENTICATED)));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    boolean isMatchedExcludedPath(String path) {
        for (String excludedPath : excludedPaths) {
            String fullExcludedPath = apiPrefix + excludedPath;
            if (path.matches(fullExcludedPath)) {
                return true;
            }
        }
        return false;
    }
}
