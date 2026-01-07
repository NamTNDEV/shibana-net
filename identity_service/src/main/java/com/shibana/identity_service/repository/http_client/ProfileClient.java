package com.shibana.identity_service.repository.http_client;

import com.shibana.identity_service.config.AuthenticationRequestInterceptor;
import com.shibana.identity_service.dto.request.ProfileCreationRequest;
import com.shibana.identity_service.dto.response.ProfileCreationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "profile-service",
        url = "http://localhost:8081/profile"
)
public interface ProfileClient {
    @PostMapping(value = "/internal/", produces = MediaType.APPLICATION_JSON_VALUE)
    ProfileCreationResponse createProfile(@RequestBody ProfileCreationRequest request);
}
