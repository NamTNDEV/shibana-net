package com.shibana.post_service.service;

import com.shibana.post_service.dto.response.PostResponse;
import com.shibana.post_service.dto.resquest.PostCreationRequest;
import com.shibana.post_service.entity.Post;
import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.mapper.PostMapper;
import com.shibana.post_service.repo.PostRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostRepo postRepo;
    PostMapper postMapper;

    public void test() {
        log.info("Post Service is up and running!");
    }

    public PostResponse createPost(PostCreationRequest postCreationRequest, String authorId) {
        Post post = Post.builder()
                .authorId(authorId)
                .content(postCreationRequest.getContent())
                .build();
        post = postRepo.save(post);
        return postMapper.toPostResponse(post);
    }

    public List<PostResponse> getAllPosts() {
        return postRepo.findAll().stream().map(postMapper::toPostResponse).toList();
    }

    public PostResponse getPostById(String postId) {
        Post post = findByPostId(postId);
        return postMapper.toPostResponse(post);
    }

    public void deletePostById(String postId) {
        Post post = findByPostId(postId);
        postRepo.delete(post);
    }

    public List<PostResponse> getPostsByAuthorId(String authorId) {
        return postRepo.findByAuthorId(authorId).stream()
                .map(postMapper::toPostResponse)
                .toList();
    }

    private Post findByPostId(String postId) {
        return postRepo.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    }
}
