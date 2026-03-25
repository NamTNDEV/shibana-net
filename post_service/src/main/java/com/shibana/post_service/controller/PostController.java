package com.shibana.post_service.controller;

import com.shibana.post_service.model.dto.response.ApiResponse;
import com.shibana.post_service.model.dto.response.external.PostResponse;
import com.shibana.post_service.model.dto.resquest.PostCreationRequestBody;
import com.shibana.post_service.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class PostController {
    PostService postService;

    @PostMapping("")
    public ApiResponse<PostResponse> createPost(
            @Validated @RequestBody PostCreationRequestBody body,
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info(":: Create Post Controller ::");
        String authorId = jwt.getClaim("user_id");
        return ApiResponse.<PostResponse>builder()
                .code(201)
                .data(postService.createPost(body, authorId))
                .message("Post created successfully")
                .build();
    }

    @GetMapping("/feed")
    public ApiResponse<List<PostResponse>> getNewsfeed() {
        log.info(":: Get Newsfeed Controller ::");
        return ApiResponse.<List<PostResponse>>builder()
                .code(200)
                .message("Posts retrieved successfully")
                .data(null)
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
                .message("Posts retrieved successfully")
                .data(postService.getPostById(postId, authorId))
                .build();
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> getAllPosts(@PathVariable String postId) {
        log.info(":: Delete Post Controller ::");
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Posts retrieved successfully")
                .build();
    }

    @GetMapping("/users/{authorId}")
    public ApiResponse<List<PostResponse>> getFeedByAuthorId(@PathVariable String authorId) {
        log.info(":: Get Posts By Author Id Controller ::");
        return ApiResponse.<List<PostResponse>>builder()
                .code(200)
                .message("Posts retrieved successfully")
                .data(new ArrayList<>())
                .build();
    }

    @GetMapping("/hashtags/{tag}")
    public ApiResponse<List<PostResponse>> getFeedByHashtag(@PathVariable String tag) {
        log.info(":: Get Posts By Hashtag Controller ::");
        return ApiResponse.<List<PostResponse>>builder()
                .code(200)
                .message("Posts retrieved successfully")
                .data(new ArrayList<>())
                .build();
    }

}
