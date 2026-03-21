package com.shibana.social_service.controller.public_controller;

import com.shibana.social_service.dto.response.ApiResponse;
import com.shibana.social_service.service.FriendShipService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/friendships")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendshipController {
    FriendShipService  friendShipService;

    @PostMapping("/send-request/{recieverId}")
    public ApiResponse<Void> sendRequest(@Validated @PathVariable String recieverId) {
        friendShipService.sendAddFriendRequest(recieverId);
        return ApiResponse.<Void>builder()
                .code(201)
                .message("Friend request sent successfully!")
                .build();
    }

    @PostMapping("/accept-request/{requesterId}")
    public ApiResponse<Void> acceptRequest(@PathVariable String requesterId) {
        friendShipService.acceptFriendRequest(requesterId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Friend request accepted successfully!")
                .build();
    }

    @PostMapping("/reject-request/{requesterId}")
    public ApiResponse<Void> rejectRequest(@PathVariable String requesterId) {
        friendShipService.rejectFriendRequest(requesterId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Friend request rejected successfully!")
                .build();
    }

    @DeleteMapping("/unfriend/{unfriendeeId}")
    public ApiResponse<Void> unfriend(@PathVariable String unfriendeeId) {
        friendShipService.unfriend(unfriendeeId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Unfriend successfully!")
                .build();
    }
}
