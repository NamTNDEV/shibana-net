package com.shibana.post_service.service;

import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.http_client.SocialClient;
import com.shibana.post_service.mapper.PostMapper;
import com.shibana.post_service.model.dto.response.PageResponse;
import com.shibana.post_service.model.dto.response.PostResponse;
import com.shibana.post_service.model.entity.Author;
import com.shibana.post_service.model.entity.Post;
import com.shibana.post_service.repo.PostRepo;
import com.shibana.post_service.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostQueryService {
    PostRepo postRepo;
    PostMapper postMapper;
    SocialClient socialClient;
    AuthorService authorService;

    public long getTotalPostCount() {
        return postRepo.findAll().size();
    }

    public Post getPostById(UUID postId) {
        return postRepo.findById(postId).orElseThrow(
                () -> {
                    log.error("Post with id {} not found", postId);
                    return new AppException(ErrorCode.POST_NOT_FOUND);
                }
        );
    }

    public PostResponse getPostByIdFromViewer(UUID postId) {
        Post post = getPostById(postId);
        UUID requesterId = SecurityUtils.getCurrentUserId();
        Author author = authorService.findExistedAuthor(post.getAuthorId());

        if (Objects.equals(author.getUserId(), requesterId)) {
            log.info(":: This user is author ::");
            return postMapper.toPostResponse(post, author);
        }

        switch (post.getPrivacy()) {
            case PUBLIC -> {
                log.info(":: This post is public ::");
                return postMapper.toPostResponse(post, author);
            }
            case PRIVATE -> {
                log.error("User {} attempted to access private post {}", requesterId, postId);
                throw new AppException(ErrorCode.POST_ACCESS_DENIED);
            }
            case FRIENDS -> {
                Boolean isFriend = socialClient.checkFriendship(requesterId, author.getUserId()).getData();
                if (!Boolean.TRUE.equals(isFriend)) {
                    log.error("User {} attempted to access friendship post {}", requesterId, postId);
                    throw new AppException(ErrorCode.POST_ACCESS_DENIED);
                }

                log.info(":: This user is friend of the author ::");
                return postMapper.toPostResponse(post, author);
            }
            default -> throw new AppException(ErrorCode.POST_ACCESS_DENIED);
        }
    }

    public PageResponse<PostResponse> getFeedByAuthorId(String authorId, String requesterId, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

//        List<PostPrivacyEnum> allowedPrivacies = new ArrayList<>();
//        allowedPrivacies.add(PostPrivacyEnum.PUBLIC);

//        if (authorId.equals(requesterId)) {
//            allowedPrivacies.add(PostPrivacyEnum.PRIVATE);
//            allowedPrivacies.add(PostPrivacyEnum.FRIENDS);
//        } else {
//            Boolean isFriend = socialClient.checkFriendship(authorId, requesterId).getData();
//            if (Boolean.TRUE.equals(isFriend)) {
//                allowedPrivacies.add(PostPrivacyEnum.FRIENDS);
//            }
//        }

//        Slice<Post> postsSlice = postRepo.getPostsByAuthorUserIdAndPrivacyIn(authorId, allowedPrivacies, pageable);
//        List<PostResponse> postResponses = postsSlice.getContent().stream()
//                .map(postMapper::toPostResponse)
//                .toList();

//        return PageResponse.<PostResponse>builder()
//                .page(page)
//                .size(size)
//                .hasNext(postsSlice.hasNext())
//                .payload(postResponses)
//                .build();

        return null;
    }

    public PageResponse<PostResponse> getFeedByHashtag(String tag, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

//        Slice<Post> feed = postRepo.getPostByHashtagAndPrivacy(tag, PostPrivacyEnum.PUBLIC, pageable);
//        List<PostResponse> postResponses = feed.getContent().stream()
//                .map(postMapper::toPostResponse)
//                .toList();

//        return PageResponse.<PostResponse>builder()
//                .page(page)
//                .size(size)
//                .hasNext(feed.hasNext())
//                .payload(postResponses)
//                .build();

        return null;
    }

    public boolean checkPostIsExist(String postId) {
//        return postRepo.getPostById(postId).isPresent();
        return false;
    }

}
