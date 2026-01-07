package com.shibana.profile_service.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {
    @NonFinal
    @Value("${jwt.issuer}")
    String ISSUER;

    @NonFinal
    @Value("${jwt.access.secret-key}")
    String ACCESS_SECRET_KEY_B64;

    String[] PUBLIC_URLS = {
            "/profiles/**",
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(PUBLIC_URLS).permitAll()
                                .anyRequest().authenticated())
                .oauth2ResourceServer(
                        oauth2 -> oauth2
                                .jwt(
                                        jwt -> jwt
                                                .decoder(jwtDecoder())
                                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                                )
                                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                )
        ;

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        byte[] secretKeyBytes = Base64.getDecoder().decode(ACCESS_SECRET_KEY_B64);
        SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, MacAlgorithm.HS512.toString());
        NimbusJwtDecoder nimbus = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();

        var defaultValidator = JwtValidators.createDefaultWithIssuer(ISSUER);
        nimbus.setJwtValidator(defaultValidator);
        return nimbus;
    }

}
