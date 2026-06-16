package com.shibana.post_service.controller;

import com.shibana.post_service.model.dto.response.ApiResponse;
import com.shibana.post_service.model.dto.resquest.ReactionRequestBody;
import com.shibana.post_service.model.enums.ReactionTargetTypeEnum;
import com.shibana.post_service.service.ReactionService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reactions")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ReactionController {
    ReactionService reactionService;

    @PostMapping("/{targetType}/{targetId}")
    public ApiResponse<Void> toggleReaction(
            @PathVariable ReactionTargetTypeEnum targetType,
            @PathVariable String targetId,
            @Validated @RequestBody ReactionRequestBody body,
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader("X-User-Id") String userId
    ) {
        log.info(":: Toggle reaction Controller ::");

        UUID requesterUUID = UUID.fromString(userId);
        UUID targetUUID = UUID.fromString(targetId);

        reactionService.handleReactionV1(requesterUUID, targetUUID, targetType, body.getReactionType());
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Reaction toggled successfully")
                .build();
    }

    ;
}