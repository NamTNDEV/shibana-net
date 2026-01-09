package com.shibana.post_service.service;

import com.shibana.post_service.dto.response.PostResponse;
import com.shibana.post_service.dto.resquest.PostCreationRequest;
import com.shibana.post_service.entity.Post;
import com.shibana.post_service.enums.RoleEnum;
import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.mapper.PostMapper;
import com.shibana.post_service.repo.PostRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostRepo postRepo;
    PostMapper postMapper;

    public PostResponse createPost(PostCreationRequest postCreationRequest) {
        if(postCreationRequest.getContent() == null || postCreationRequest.getContent().isBlank()) {
            log.error("Content is missing");
            throw new AppException(ErrorCode.INVALID_POST_DATA);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String authorId = jwt.getClaim("user_id");

        Post post = Post.builder()
                .authorId(authorId)
                .content(postCreationRequest.getContent())
                .build();
        post = postRepo.save(post);
        return postMapper.toPostResponse(post);
    }

    public List<PostResponse> getAllPosts() {
        List<Post> posts = postRepo.findAll();
        List<PostResponse> postResponses = new ArrayList<>();
        for(Post post : posts) {
            postResponses.add(postMapper.toPostResponse(post));
        }
        return postResponses;
    }

    public PostResponse getPostById(String postId) {
        Post post = findByPostId(postId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String role = jwt.getClaim("scope");
        String authorId = jwt.getClaim("user_id");

        if (isNotValidRole(role, RoleEnum.ADMIN.toString()) && !post.getAuthorId().equals(authorId)) {
            log.error("User is not authorized to view this post");
            throw new AppException(ErrorCode.FORBIDDEN_OPERATION);
        }

        return postMapper.toPostResponse(post);
    }

    public void deletePostById(String postId) {
        Post post = findByPostId(postId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String role = jwt.getClaim("scope");
        String authorId = jwt.getClaim("user_id");

        if (isNotValidRole(role, RoleEnum.ADMIN.toString()) && !post.getAuthorId().equals(authorId)) {
            log.error("User is not authorized to delete this post");
            throw new AppException(ErrorCode.FORBIDDEN_OPERATION);
        }

        postRepo.deleteById(postId);
    }

    public List<PostResponse> getPostsByAuthorId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String authorId = jwt.getClaim("user_id");

        List<Post> posts = postRepo.findByAuthorId(authorId);
        List<PostResponse> postResponses = new ArrayList<>();
        for(Post post : posts) {
            postResponses.add(postMapper.toPostResponse(post));
        }
        return postResponses;
    }

    boolean isNotValidRole(String role, String ValidRole) {
        return !role.contains(ValidRole);
    }

    public Post findByPostId(String postId) {
        return postRepo.findById(postId)
                .orElseThrow(() -> {
                    return new AppException(ErrorCode.POST_NOT_FOUND);
                });
    }
}
