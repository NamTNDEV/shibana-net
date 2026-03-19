package com.shibana.social_service.controller.public_controller;

import com.shibana.social_service.dto.request.FollowRequestBody;
import com.shibana.social_service.dto.response.ApiResponse;
import com.shibana.social_service.service.FollowService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/follows")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FollowController {
    FollowService followService;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/{followeeId}")
    public ApiResponse<Boolean> checkIsFollowing(@PathVariable String followeeId) {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .data(followService.checkIsFollowing(followeeId))
                .message("Success")
                .build();
    }

    @PostMapping("")
    public ApiResponse<Void> follow(
            @Validated @RequestBody FollowRequestBody requestBody
    ) {
        followService.follow(requestBody.getFolloweeId());
        return ApiResponse.<Void>builder()
                .code(201)
                .message("Follow successfully!")
                .build();
    }

    @DeleteMapping("/{followeeId}")
    public ApiResponse<Void> unfollow(@PathVariable String followeeId) {
        followService.unfollow(followeeId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Unfollow successfully!")
                .build();
    }
}
