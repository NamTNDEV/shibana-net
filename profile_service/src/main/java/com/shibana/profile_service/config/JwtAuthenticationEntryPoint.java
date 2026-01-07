package com.shibana.profile_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shibana.profile_service.dto.response.ApiResponse;
import com.shibana.profile_service.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("Unauthorized access attempt:: {}", authException.getMessage());
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .build();
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }

}
