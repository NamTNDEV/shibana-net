package com.shibana.post_service.http_client;

import com.shibana.post_service.model.dto.response.ApiResponse;
import com.shibana.post_service.model.embedded.Author;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "social-service",
        url = "http://localhost:8081/social/internal"
)
public interface SocialClient {
    @GetMapping("/profiles/{userId}/author-profile")
    ApiResponse<Author> getAuthorProfileByUserId(@PathVariable String userId);

    @GetMapping("/friendships/check")
    ApiResponse<Boolean> checkFriendship(
            @RequestParam String viewerId,
            @RequestParam String authorId
    );
}
