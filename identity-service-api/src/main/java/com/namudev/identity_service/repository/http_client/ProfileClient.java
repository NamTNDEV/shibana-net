package com.namudev.identity_service.repository.http_client;

import com.namudev.identity_service.dto.request.ProfileCreationRequest;
import com.namudev.identity_service.dto.response.ProfileCreationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "profile-service",
        url = "http://localhost:8081/api/v1/profile"
)
public interface ProfileClient {
    @PostMapping(value = "/")
    ProfileCreationResponse createProfile(@RequestBody ProfileCreationRequest request);
}
