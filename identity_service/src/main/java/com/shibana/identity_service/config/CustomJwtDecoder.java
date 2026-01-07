package com.shibana.identity_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

@Slf4j
public record CustomJwtDecoder(
        JwtDecoder jwtDecoder
) implements JwtDecoder {
    @Override
    public Jwt decode(String token) throws JwtException {
        Jwt jwt;

        try {
            jwt = jwtDecoder.decode(token);
        } catch (JwtException e) {
            log.error("Jwt decode error::", e);
            throw e;
        }

        return jwt;
    }
}
