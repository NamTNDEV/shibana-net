package com.shibana.identity_service.repository.http_client;

import com.shibana.identity_service.dto.request.ProfileCreationRequest;
import com.shibana.identity_service.dto.response.ApiResponse;
import com.shibana.identity_service.dto.response.ProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "profile-service",
        url = "http://localhost:8081/profile"
)
public interface ProfileClient {
    @PostMapping(value = "/internal/", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<ProfileResponse> createProfile(@RequestBody ProfileCreationRequest request);

    @GetMapping("/internal/{userId}")
    ApiResponse<ProfileResponse> getProfileByUserId(@PathVariable String userId);
}
