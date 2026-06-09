package com.shibana.post_service.controller;

import com.shibana.post_service.model.dto.response.ApiResponse;
import com.shibana.post_service.model.dto.response.CommentResponse;
import com.shibana.post_service.model.dto.response.CursorResponse;
import com.shibana.post_service.model.dto.resquest.CommentUpdateRequestBody;
import com.shibana.post_service.model.dto.resquest.RootCreationRequestBody;
import com.shibana.post_service.model.service_command.comments.CommentRootCreationCommand;
import com.shibana.post_service.model.service_command.comments.CommentUpdateCommand;
import com.shibana.post_service.model.service_command.comments.ReplyCommentCreationCommand;
import com.shibana.post_service.service.CommentService;
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
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    /**
     * CREATE ROOT COMMENT: POST /posts/{postId}/comments
     */
    @PostMapping("/{postId}/comments")
    public ApiResponse<CommentResponse> createRootComment(
            @PathVariable String postId,
            @Validated @RequestBody RootCreationRequestBody body,
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info(":: Create root comment controller ::");
        UUID authorId = UUID.fromString(jwt.getClaim("user_id"));
        UUID postUUID = UUID.fromString(postId);

        var command = new CommentRootCreationCommand(
                postUUID,
                body.getContent(),
                authorId
        );

        return ApiResponse.<CommentResponse>builder()
                .code(201)
                .message("Comment created successfully")
                .data(commentService.createRootComment(command))
                .build();
    }

    /**
     * CREATE REPLY COMMENT: POST /comments/{parentId}/replies
     */
    @PostMapping("/comments/{parentId}/replies")
    public ApiResponse<CommentResponse> createReplyComment(
            @PathVariable String parentId,
            @Validated @RequestBody RootCreationRequestBody body,
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info(":: Create reply comment controller ::");
        UUID authorUUID = UUID.fromString(jwt.getClaim("user_id"));
        UUID parentUUID = UUID.fromString(parentId);

        var command = new ReplyCommentCreationCommand(
                body.getContent(),
                parentUUID,
                authorUUID
        );

        return ApiResponse.<CommentResponse>builder()
                .code(201)
                .message("Reply comment created successfully")
                .data(commentService.createReplyComment(command))
                .build();
    }

    /**
     * GET ROOT COMMENTS: GET /posts/{postId}/comments?cursor={cursor}&size={size}
     */
    @GetMapping("/{postId}/comments")
    public ApiResponse<CursorResponse<CommentResponse>> getRootComments(
            @PathVariable String postId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info(":: Get root comments controller ::");
        UUID postUUID = UUID.fromString(postId);

        return ApiResponse.<CursorResponse<CommentResponse>>builder()
                .code(200)
                .message("Retrieve comments successfully")
                .data(commentService.getRootCommentsByPostId(postUUID, cursor, size))
                .build();
    }

    /**
     * GET ROOT COMMENTS: GET /posts/comments/{parentId}/replies?cursor={cursor}&size={size}
     */
    @GetMapping("/comments/{parentId}/replies")
    public ApiResponse<CursorResponse<CommentResponse>> getReplies(
            @PathVariable String parentId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info(":: Get replies controller ::");
        UUID parentUUID = UUID.fromString(parentId);

        return ApiResponse.<CursorResponse<CommentResponse>>builder()
                .code(200)
                .message("Retrieve replies successfully")
                .data(commentService.getRepliesByCommentId(parentUUID, cursor, size))
                .build();
    }

    @PutMapping("/comments/{id}")
    public ApiResponse<CommentResponse> updateComment(
            @PathVariable String id,
            @RequestBody CommentUpdateRequestBody body,
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info(":: Update comment controller ::");
        UUID requesterUUID = UUID.fromString(jwt.getClaim("user_id"));
        UUID commentUUID = UUID.fromString(id);
        CommentUpdateCommand command = new CommentUpdateCommand(
                requesterUUID,
                commentUUID,
                body.getNewContent()
        );

        return ApiResponse.<CommentResponse>builder()
                .code(200)
                .message("Update comment successfully")
                .data(commentService.updateComment(command))
                .build();
    }
//
//    @DeleteMapping("/comments/{commentId}")
//    public ApiResponse<Void> deleteComment(
//            @PathVariable String commentId,
//            @AuthenticationPrincipal Jwt jwt
//    ) {
//        log.info(":: Delete comment controller ::");
//        String authorId = jwt.getClaim("user_id").toString();
//        commentService.deleteComment(authorId, commentId);
//        return ApiResponse.<Void>builder()
//                .code(200)
//                .message("Delete comment successfully")
//                .build();
//    }
}
