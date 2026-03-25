package com.shibana.social_service.controller.internal_controller;

import com.shibana.social_service.dto.response.ApiResponse;
import com.shibana.social_service.service.FriendShipService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/internal/friendships")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class InternalFriendshipController {
    FriendShipService friendShipService;

    @GetMapping("/check")
    ApiResponse<Boolean> checkFriendship(
            @RequestParam String viewerId,
            @RequestParam String authorId
    ) {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message("Friendship status retrieved successfully")
                .data(friendShipService.checkIsFriend(viewerId, authorId))
                .build();
    }
}
