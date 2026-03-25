package com.shibana.post_service.service;

import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.http_client.SocialClient;
import com.shibana.post_service.mapper.PostMapper;
import com.shibana.post_service.model.dto.response.PageResponse;
import com.shibana.post_service.model.dto.response.PostResponse;
import com.shibana.post_service.model.dto.resquest.PostCreationRequestBody;
import com.shibana.post_service.model.dto.resquest.PostUpdateRequestBody;
import com.shibana.post_service.model.embedded.Author;
import com.shibana.post_service.model.entity.Post;
import com.shibana.post_service.model.enums.PostPrivacyEnum;
import com.shibana.post_service.repo.PostRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    public PostResponse getPostById(String postId, String authorId) {
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
                Boolean isFriend = socialClient.checkFriendship(authorId, post.getAuthor().getUserId()).getData();
                if (!Boolean.TRUE.equals(isFriend)) {
                    log.error("User {} attempted to access friendship post {}", authorId, postId);
                    throw new AppException(ErrorCode.POST_ACCESS_DENIED);
                }

                log.info(":: This user is friend of the author ::");
                return postMapper.toPostResponse(post);
            }
            default -> throw new AppException(ErrorCode.POST_ACCESS_DENIED);
        }
    }

    @Transactional
    public void updatePostById(String postId, String authorId, PostUpdateRequestBody body) {
        String content = body.getContent();
        PostPrivacyEnum privacy = body.getPrivacy();

        Post post = postRepo.getPostById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getAuthor().getUserId().equals(authorId)) {
            log.error("User {} attempted to update post {} which they do not own", authorId, postId);
            throw new AppException(ErrorCode.POST_UPDATE_DENIED);
        }

        if (!post.getContent().equals(body.getContent()) || post.getPrivacy() != body.getPrivacy()) {
            log.info("Updating post {} for user {}. New content: {}, New privacy: {}", postId, authorId, content, privacy);
            post.setContent(content);
            post.setPrivacy(privacy);
            postRepo.save(post);
        } else {
            log.info("The update content and privacy are the same as the existing ones. No update performed for post {}", postId);
        }
    }

    @Transactional
    public void deleteById(String postId, String authorId) {
        Post post = postRepo.getPostById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getAuthor().getUserId().equals(authorId)) {
            log.error("User {} attempted to delete post {} which they do not own", authorId, postId);
            throw new AppException(ErrorCode.POST_DELETE_DENIED);
        }

        postRepo.deleteById(postId);
    }

    public PageResponse<PostResponse> getFeedByAuthorId(String authorId, String requesterId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<PostPrivacyEnum> allowedPrivacies = new ArrayList<>();
        allowedPrivacies.add(PostPrivacyEnum.PUBLIC);

        if (authorId.equals(requesterId)) {
            allowedPrivacies.add(PostPrivacyEnum.PRIVATE);
            allowedPrivacies.add(PostPrivacyEnum.FRIENDS);
        } else {
            Boolean isFriend = socialClient.checkFriendship(authorId, requesterId).getData();
            if (Boolean.TRUE.equals(isFriend)) {
                allowedPrivacies.add(PostPrivacyEnum.FRIENDS);
            }
        }

        Slice<Post> postsSlice = postRepo.getPostsByAuthorUserIdAndPrivacyIn(authorId, allowedPrivacies, pageable);
        List<PostResponse> postResponses = postsSlice.getContent().stream()
                .map(postMapper::toPostResponse)
                .toList();

        return PageResponse.<PostResponse>builder()
                .page(page)
                .size(size)
                .hasNext(postsSlice.hasNext())
                .payload(postResponses)
                .build();
    }

    public PageResponse<PostResponse> getFeedByHashtag(String tag, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Slice<Post> feed = postRepo.getPostByHashtagAndPrivacy(tag, PostPrivacyEnum.PUBLIC, pageable);
        List<PostResponse> postResponses = feed.getContent().stream()
                .map(postMapper::toPostResponse)
                .toList();

        return PageResponse.<PostResponse>builder()
                .page(page)
                .size(size)
                .hasNext(feed.hasNext())
                .payload(postResponses)
                .build();
    }
}
