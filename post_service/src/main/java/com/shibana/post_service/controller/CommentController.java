package com.shibana.post_service.controller;

import com.shibana.post_service.model.dto.response.ApiResponse;
import com.shibana.post_service.model.dto.response.CommentResponse;
import com.shibana.post_service.model.dto.response.PageResponse;
import com.shibana.post_service.model.dto.resquest.CommentCreationRequestBody;
import com.shibana.post_service.model.service_command.comments.CommentCreationCommand;
import com.shibana.post_service.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @PostMapping("/comments")
    public ApiResponse<Void> createComment(
            @Validated @RequestBody CommentCreationRequestBody body,
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info(":: Create comment controller ::");
        String authorId = jwt.getClaim("user_id").toString();

        var command = new CommentCreationCommand(
                body.getPostId(),
                body.getContent(),
                body.getParentId(),
                authorId
        );
        commentService.createComment(command);
        return ApiResponse.<Void>builder()
                .code(201)
                .message("Comment created successfully")
                .build();
    }

    @GetMapping("/{postId}/comments")
    public ApiResponse<PageResponse<CommentResponse>> getRootComments(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info(":: Get comments controller ::");
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .code(200)
                .message("Retrieve comments successfully")
                .data(commentService.getRootCommentsByPostId(postId, page, size))
                .build();
    }

    @GetMapping("/{postId}/comments/{commentId}/replies")
    public ApiResponse<PageResponse<CommentResponse>> getReplies(
            @PathVariable String postId,
            @PathVariable String commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info(":: Get replies controller ::");
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .code(200)
                .message("Retrieve replies successfully")
                .data(commentService.getRepliesByCommentId(postId, commentId, page, size))
                .build();
    }
}
