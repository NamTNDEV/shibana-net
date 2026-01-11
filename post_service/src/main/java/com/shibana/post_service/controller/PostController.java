package com.shibana.post_service.controller;

import com.shibana.post_service.dto.response.ApiResponse;
import com.shibana.post_service.dto.response.PostResponse;
import com.shibana.post_service.dto.resquest.PostCreationRequest;
import com.shibana.post_service.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class PostController {
    PostService postService;

    @GetMapping("/test")
    public ApiResponse<?> test() {
        postService.test();
        return ApiResponse.<String>builder()
                .code(200)
                .message("Test Controller is working...")
                .build();
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<PostResponse>> getAllPostsByAdmin() {
        log.info("Get All Posts Request...");
        return ApiResponse.<List<PostResponse>>builder()
                .code(200)
                .message("Posts retrieved successfully")
                .data(postService.getAllPosts())
                .build();
    }

    @GetMapping("/{postId}")
    @PreAuthorize("@postPolicy.canViewPost(#postId, authentication)")
    public ApiResponse<PostResponse> getPostById(@PathVariable String postId) {
        log.info("Get Post By Id Request...");
        return ApiResponse.<PostResponse>builder()
                .code(200)
                .message("Post retrieved successfully")
                .data(postService.getPostById(postId))
                .build();
    }

    @GetMapping("/my-posts")
    public ApiResponse<List<PostResponse>> getAllMyPosts(@AuthenticationPrincipal Jwt jwt) {
        log.info("Get My Posts Request...");
        String authorId = jwt.getClaimAsString("user_id");
        return ApiResponse.<List<PostResponse>>builder()
                .code(200)
                .message("Posts retrieved successfully")
                .data(postService.getPostsByAuthorId(authorId))
                .build();
    }

    @PostMapping("/")
    public ApiResponse<PostResponse> createPost(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody PostCreationRequest postCreationRequest
    ) {
        log.info("Post Creation Request...");
        String authorId = jwt.getClaimAsString("user_id");
        return ApiResponse.<PostResponse>builder()
                .code(201)
                .message("Post created successfully")
                .data(postService.createPost(postCreationRequest, authorId))
                .build();
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("@postPolicy.canViewPost(#postId, authentication)")
    public ApiResponse<?> deletePost(@PathVariable String postId) {
        log.info("Post Deletion Request...");
        postService.deletePostById(postId);
        return ApiResponse.builder()
                .code(200)
                .message("Post deleted successfully")
                .build();
    }

}
