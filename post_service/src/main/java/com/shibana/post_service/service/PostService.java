package com.shibana.post_service.service;

import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.http_client.SocialClient;
import com.shibana.post_service.mapper.PostMapper;
import com.shibana.post_service.model.dto.response.external.PostResponse;
import com.shibana.post_service.model.dto.resquest.PostCreationRequestBody;
import com.shibana.post_service.model.embedded.Author;
import com.shibana.post_service.model.entity.Post;
import com.shibana.post_service.model.enums.PostPrivacyEnum;
import com.shibana.post_service.repo.PostRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostRepo postRepo;
    PostMapper postMapper;
    SocialClient socialClient;

    @Transactional
    public PostResponse createPost(PostCreationRequestBody body, String authorId) {
        String content = body.getContent();
        var privacy = body.getPrivacy();

        Author author = socialClient.getAuthorProfileByUserId(authorId).getData();
        Post post = Post.builder()
                .content(content)
                .author(author)
                .privacy(privacy)
                .build();
        Post createdPost = postRepo.save(post);
        return postMapper.toPostResponse(createdPost);
    }

    public PostResponse getPostById(String postId,  String authorId) {
        Post post = postRepo.getPostById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (post.getAuthor().getUserId().equals(authorId)) {
            log.info(":: This user is author ::");
            return postMapper.toPostResponse(post);
        }

        switch (post.getPrivacy()) {
            case PUBLIC -> {
                log.info(":: This post is public ::");
                return postMapper.toPostResponse(post);
            }
            case PRIVATE -> {
                log.error("User {} attempted to access private post {}", authorId, postId);
                throw new AppException(ErrorCode.POST_ACCESS_DENIED);
            }
            case FRIENDS -> {
                boolean isFriend = socialClient.checkFriendship(authorId, post.getAuthor().getUserId()).getData();
                if (!isFriend) {
                    log.error("User {} attempted to access friendship post {}", authorId, postId);
                    throw new AppException(ErrorCode.POST_ACCESS_DENIED);
                }

                log.info(":: This user is friend of the author ::");
                return postMapper.toPostResponse(post);
            }
            default -> throw new AppException(ErrorCode.POST_ACCESS_DENIED);
        }
    }
}
