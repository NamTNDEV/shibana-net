package com.shibana.api_gateway.service;

import com.shibana.api_gateway.dto.request.IntrospectRequest;
import com.shibana.api_gateway.dto.response.ApiResponse;
import com.shibana.api_gateway.dto.response.IntrospectResponse;
import com.shibana.api_gateway.httpClient.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    IdentityClient identityClient;

    public Mono<ApiResponse<IntrospectResponse>> introspectToken(String request) {
        log.info("Token is being introspected...");
        return identityClient.introspectToken(
                IntrospectRequest.builder()
                        .token(request)
                        .build()
        );
    };
}
