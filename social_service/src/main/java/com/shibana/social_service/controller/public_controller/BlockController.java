package com.shibana.social_service.controller.public_controller;

import com.shibana.social_service.dto.response.ApiResponse;
import com.shibana.social_service.service.BlockService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/blocks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlockController {
    BlockService blockService;

    @PostMapping("/{blockeeId}")
    public ApiResponse<Void> block(@PathVariable String blockeeId) {
        blockService.blockUser(blockeeId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("User blocked successfully")
                .build();
    }

    @DeleteMapping("/{blockeeId}")
    public ApiResponse<Void> unblock(@PathVariable String blockeeId) {
        blockService.unblockUser(blockeeId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("User unblocked successfully")
                .build();
    }
}
