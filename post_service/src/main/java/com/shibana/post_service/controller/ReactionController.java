package com.shibana.post_service.controller;

import com.shibana.post_service.messaging.dto.payloads.ReactedPayload;
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

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reactions")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ReactionController {
    ReactionService reactionService;

    @PostMapping("/v0/{targetType}/{targetId}")
    public ApiResponse<Void> toggleReactionV0(@PathVariable ReactionTargetTypeEnum targetType, @PathVariable String targetId, @Validated @RequestBody ReactionRequestBody body, @AuthenticationPrincipal Jwt jwt) {
        for (int n : List.of(10, 50, 100, 500, 1000, 2000, 3000, 5000, 10000)) {
        List<ReactedPayload> testBatch = reactionService.generateFakePayloads(n);
        long t0 = System.currentTimeMillis();
        reactionService.batchUpsertToDb(testBatch);
        long t1 = System.currentTimeMillis();
        log.info("Batch {} records: {}ms ({}ms/record)", n, t1 - t0, (double) (t1 - t0) / n);
    }
        return ApiResponse.<Void>builder().code(200).message("Reaction toggled successfully").build();
    }

    @PostMapping("/v1/{targetType}/{targetId}")
    public ApiResponse<Void> toggleReactionV1(
            @PathVariable String targetId,
            @AuthenticationPrincipal Jwt jwt,
            @Validated @RequestBody ReactionRequestBody body,
            @PathVariable ReactionTargetTypeEnum targetType
    ) {
        UUID requesterUUID = UUID.fromString(jwt.getClaim("user_id"));
        UUID targetUUID = UUID.fromString(targetId);
        reactionService.handleReactionV1(requesterUUID, targetUUID, targetType, body.getReactionType());
        return ApiResponse.<Void>builder().code(200).message("Reaction toggled successfully").build();
    }

    @PostMapping("/v2/{targetType}/{targetId}")
    public ApiResponse<Void> toggleReactionV2(
            @PathVariable ReactionTargetTypeEnum targetType,
            @PathVariable String targetId,
            @Validated @RequestBody ReactionRequestBody body,
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader("x-user-id") String userId
    ) {
        UUID requesterUUID = UUID.fromString(userId);
        UUID targetUUID = UUID.fromString(targetId);

        var message = reactionService.handleReactionV2(requesterUUID, targetUUID, targetType, body.getReactionType());
        return ApiResponse.<Void>builder().code(200).message(message).build();
    }
}