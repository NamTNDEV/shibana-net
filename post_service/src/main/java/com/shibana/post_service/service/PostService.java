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
import com.shibana.post_service.model.service_command.posts.PostCreationCommand;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public PostResponse createPost(PostCreationCommand  postCreationCommand) {
        String content = postCreationCommand.content();
        var privacy = postCreationCommand.privacy();

        Author author = socialClient.getAuthorProfileByUserId(postCreationCommand.authorId()).getData();
        Post post = Post.builder()
                .content(content)
                .author(author)
                .hashtags(extractHashtagsFromContent(content))
                .privacy(privacy)
                .build();
        Post createdPost = postRepo.save(post);
        return postMapper.toPostResponse(createdPost);
    }

    public Post getPostById(String postId) {
        return postRepo.getPostById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    }

    public PostResponse getPostByIdFromViewer(String postId, String authorId) {
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
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

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

    public boolean checkPostIsExist(String postId) {
        return postRepo.getPostById(postId).isPresent();
    }

    public void increaseCommentCount(String postId) {
        postRepo.incCommentCount(postId);
    }

    public PageResponse<PostResponse> getNewsfeed(String requesterId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        var targetData = socialClient.getNewsfeedTargertersId(requesterId).getData();
        Set<String> friendIds = targetData.friendIds() != null ? targetData.friendIds() : Set.of();
        Set<String> followingIds = targetData.followingIds() != null ? targetData.followingIds() : Set.of();

        List<PostPrivacyEnum> allowedFriendPrivacies = List.of(PostPrivacyEnum.FRIENDS,  PostPrivacyEnum.PUBLIC);
        List<PostPrivacyEnum> allowedFollowingPrivacies = List.of(PostPrivacyEnum.PUBLIC);

        Slice<Post> posts = postRepo.getNewsfeed(
                requesterId,
                friendIds, allowedFriendPrivacies,
                followingIds, allowedFollowingPrivacies,
                pageable
        );

        List<Post> finalPosts = new ArrayList<>(posts.getContent());

        if (page == 0 && finalPosts.size() < size) {
            var fallbackPosts = getFallbackPosts(size, finalPosts);
            finalPosts.addAll(fallbackPosts);
        }

        var feed = finalPosts.stream()
                .map(postMapper::toPostResponse)
                .toList();

        return PageResponse.<PostResponse>builder()
                .payload(feed)
                .page(page)
                .size(size)
                .hasNext(posts.hasNext())
                .build();
    }

    private List<Post> getFallbackPosts(int size, List<Post> finalPosts) {
        int missingAmount = size - finalPosts.size();
        Pageable fallbackPageable = PageRequest.of(0, missingAmount, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<String> existingPostIds = finalPosts.stream()
                .map(Post::getId)
                .toList();

        List<PostPrivacyEnum> fallbackAllowedPrivacies = new ArrayList<>();
        fallbackAllowedPrivacies.add(PostPrivacyEnum.PUBLIC);

        Slice<Post> fallbackPosts = postRepo.getFallbackNewsfeed(existingPostIds, fallbackAllowedPrivacies, fallbackPageable);
        return fallbackPosts.getContent();
    }

    private List<String> extractHashtagsFromContent(String content) {
        if (content == null || content.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> hashtags = new HashSet<>();
        Pattern hashtagPattern = Pattern.compile("(?U)#\\w+");
        Matcher hashtagMatcher = hashtagPattern.matcher(content);

        while (hashtagMatcher.find()) {
            String hashtag = hashtagMatcher.group().substring(1).toLowerCase();
            hashtags.add(hashtag);
        }

        return new ArrayList<>(hashtags);
    }
}
