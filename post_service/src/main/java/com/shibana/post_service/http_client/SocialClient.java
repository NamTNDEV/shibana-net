package com.shibana.post_service.http_client;

import com.shibana.post_service.model.dto.response.ApiResponse;
import com.shibana.post_service.model.dto.response.NewsfeedTargetResponse;
import com.shibana.post_service.model.entity.Author;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "social-service",
        url = "http://localhost:8081/social/internal"
)
public interface SocialClient {
    @GetMapping("/profiles/{userId}/author-profile")
    ApiResponse<Author> getAuthorProfileByUserId(@PathVariable UUID userId);

    @GetMapping("/connections/check")
    ApiResponse<Boolean> checkFriendship(
            @RequestParam UUID viewerId,
            @RequestParam UUID authorId
    );

    @GetMapping("/connections/newsfeed-targeters/{requesterId}")
    ApiResponse<NewsfeedTargetResponse> getNewsfeedTargertersId(@PathVariable UUID requesterId);
}
