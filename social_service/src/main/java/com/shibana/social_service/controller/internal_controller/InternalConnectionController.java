package com.shibana.social_service.controller.internal_controller;

import com.shibana.social_service.dto.response.ApiResponse;
import com.shibana.social_service.dto.response.NewsfeedTargetResponse;
import com.shibana.social_service.service.ConnectionsService;
import com.shibana.social_service.service.FriendShipService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/internal/connections")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class InternalConnectionController {
    FriendShipService friendShipService;
    ConnectionsService  connectionsService;

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

    @GetMapping("/newsfeed-targeters/{requesterId}")
    ApiResponse<NewsfeedTargetResponse> getNewsfeedTargertersId(
            @PathVariable String requesterId
    ) {
        log.info(":: Get news feed targeters for user {} ::", requesterId);
        return ApiResponse.<NewsfeedTargetResponse>builder()
                .code(200)
                .message("News feed targeters retrieved successfully")
                .data(connectionsService.getNewsfeedTargeters(requesterId))
                .build();
    }

}
