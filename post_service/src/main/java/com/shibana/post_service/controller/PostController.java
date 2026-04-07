package com.shibana.post_service.controller;

import com.shibana.post_service.model.dto.response.ApiResponse;
import com.shibana.post_service.model.dto.response.PageResponse;
import com.shibana.post_service.model.dto.response.PostResponse;
import com.shibana.post_service.model.dto.resquest.PostCreationRequestBody;
import com.shibana.post_service.model.dto.resquest.PostUpdateRequestBody;
import com.shibana.post_service.model.service_command.posts.PostCreationCommand;
import com.shibana.post_service.service.NewsfeedService;
import com.shibana.post_service.service.PostCommandService;
import com.shibana.post_service.service.PostQueryService;
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
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class PostController {
    NewsfeedService newsfeedService;
    PostQueryService postQueryService;
    PostCommandService  postCommandService;

    @PostMapping("")
    public ApiResponse<PostResponse> createPost(
            @Validated @RequestBody PostCreationRequestBody body,
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info(":: Create Post Controller ::");
        String authorId = jwt.getClaim("user_id");
        var command = new PostCreationCommand(
                body.getContent(),
                authorId,
                body.getPrivacy()
        );
        return ApiResponse.<PostResponse>builder()
                .code(201)
                .data(postCommandService.createPost(command))
                .message("Created post successfully")
                .build();
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostResponse> getPostById(
            @PathVariable String postId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info(":: Get Post By Id Controller ::");
        String authorId = jwt.getClaim("user_id");
        return ApiResponse.<PostResponse>builder()
                .code(200)
                .message("Post retrieved successfully")
                .data(postQueryService.getPostByIdFromViewer(postId, authorId))
                .build();
    }

    @PutMapping("/{postId}")
    public ApiResponse<Void> updatePost(
            @PathVariable String postId,
            @Validated @RequestBody PostUpdateRequestBody body,
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info(":: Update Post Controller ::");
        String authorId = jwt.getClaim("user_id");
        postCommandService.updatePostById(postId, authorId, body);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Updated post successfully")
                .build();
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(
            @PathVariable String postId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info(":: Delete Post Controller ::");
        String authorId = jwt.getClaim("user_id");
        postCommandService.deleteById(postId, authorId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Deleted post successfully")
                .build();
    }

    @GetMapping("/newsfeed")
    public ApiResponse<PageResponse<PostResponse>> getNewsfeed(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info(":: Get Newsfeed Controller ::");
        String requesterId = jwt.getClaim("user_id");
        return ApiResponse.<PageResponse<PostResponse>>builder()
                .code(200)
                .message("Posts retrieved successfully")
                .data(newsfeedService.getNewsfeed(requesterId, page, size))
                .build();
    }

    @GetMapping("/users/{authorId}")
    public ApiResponse<PageResponse<PostResponse>> getFeedByAuthorId(
            @PathVariable String authorId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info(":: Get Posts By Author Id Controller ::");
        String requesterId = jwt.getClaim("user_id");
        return ApiResponse.<PageResponse<PostResponse>>builder()
                .code(200)
                .message("Posts retrieved successfully")
                .data(postQueryService.getFeedByAuthorId(authorId, requesterId, page, size))
                .build();
    }

    @GetMapping("/hashtags/{tag}")
    public ApiResponse<PageResponse<PostResponse>> getFeedByHashtag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info(":: Get Posts By Hashtag Controller ::");
        return ApiResponse.<PageResponse<PostResponse>>builder()
                .code(200)
                .message("Posts retrieved successfully")
                .data(postQueryService.getFeedByHashtag(tag, page, size))
                .build();
    }

}
