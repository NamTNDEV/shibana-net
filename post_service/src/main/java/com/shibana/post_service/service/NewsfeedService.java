package com.shibana.post_service.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.http_client.SocialClient;
import com.shibana.post_service.mapper.PostMapper;
import com.shibana.post_service.model.dto.response.CursorResponse;
import com.shibana.post_service.model.dto.response.PostResponse;
import com.shibana.post_service.model.entity.Author;
import com.shibana.post_service.model.entity.Post;
import com.shibana.post_service.model.enums.PostPrivacyEnum;
import com.shibana.post_service.repo.PostRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsfeedService {
    PostRepo postRepo;
    SocialClient socialClient;
    AuthorService authorService;

    UUID NIL_UUID = new UUID(0L, 0L);
    private final PostMapper postMapper;

    public CursorResponse<PostResponse> getNewsfeed(UUID requesterId, UUID cursor, int size) {
        log.info(":: Get Newsfeed Service ::");

        var targetData = socialClient.getNewsfeedTargertersId(requesterId).getData();
        Set<UUID> friendIds = targetData.friendIds() != null ? targetData.friendIds() : Set.of(NIL_UUID);
        Set<UUID> followingIds = targetData.followingIds() != null ? targetData.followingIds() : Set.of(NIL_UUID);

        Set<String> allowedFriendPrivacies = Set.of(PostPrivacyEnum.FRIENDS.name(), PostPrivacyEnum.PUBLIC.name());
        Set<String> allowedFollowingPrivacies = Set.of(PostPrivacyEnum.PUBLIC.name());

        int sizePlusOne = size + 1;
        boolean isFirstLoad = cursor == null;
        UUID actualCursor = isFirstLoad ? UuidCreator.getTimeOrderedEpoch() : cursor;

        List<Post> posts = new ArrayList<>(
                postRepo.getNewsfeed(
                        requesterId,
                        friendIds, allowedFriendPrivacies,
                        followingIds, allowedFollowingPrivacies,
                        actualCursor,
                        sizePlusOne
                )
        );

        if (isFirstLoad && posts.size() < size) {
            var fallbackPosts = getFallbackPosts(posts, size, actualCursor);
            posts.addAll(fallbackPosts);
            posts.sort(Comparator.comparing(Post::getCreatedAt).reversed());
        }

        boolean hasNext = posts.size() > size;
        if (hasNext) {
            posts.removeLast();
        }

        String nextCursor = posts.isEmpty() ? null : posts.getLast().getId().toString();

        List<PostResponse> newsfeed = mapAuthorData(posts);

        return CursorResponse.<PostResponse>builder()
                .size(newsfeed.size())
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .payload(newsfeed)
                .build();
    }

    private List<Post> getFallbackPosts(List<Post> existingPosts, int targetSize, UUID cursor) {
        int missingAmountPosts = targetSize - existingPosts.size();

        Set<UUID> existingPostIds = existingPosts.isEmpty()
                ? Set.of(NIL_UUID)
                : existingPosts.stream().map(Post::getId).collect(Collectors.toSet());

        return postRepo.getFallbackNewsfeed(
                existingPostIds,
                PostPrivacyEnum.PUBLIC.name(),
                cursor,
                missingAmountPosts
        );
    }

    private List<PostResponse> mapAuthorData(List<Post> posts) {
        if (posts.isEmpty()) {
            return Collections.emptyList();
        }

        Set<UUID> authorIds = posts.stream().map(Post::getAuthorId).collect(Collectors.toSet());
        Map<UUID, Author> authorsMap = authorService.findAllAuthors(authorIds).stream()
                .collect(Collectors.toMap(Author::getUserId, author -> author));

        return posts.stream()
                .map(
                        post -> postMapper.toPostResponse(
                                post,
                                authorsMap.getOrDefault(post.getAuthorId(), authorService.getFallbackAuthor(post.getAuthorId()))
                        )
                ).toList();
    }
}
