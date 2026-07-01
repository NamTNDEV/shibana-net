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
import com.shibana.post_service.model.enums.ReactionTargetTypeEnum;
import com.shibana.post_service.model.enums.ReactionTypeEnum;
import com.shibana.post_service.repo.PostRepo;
import com.shibana.post_service.service.cache.ReactionCacheService;
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
    ReactionCacheService reactionCacheService;

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
        List<UUID> postIds = posts.stream().map(Post::getId).toList();
        var batchReactionStats = reactionCacheService.getBatchReactionStats(postIds, ReactionTargetTypeEnum.POST);

        List<PostResponse> newsfeed = mapAuthorAndReactionData(posts, batchReactionStats);

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

    private List<PostResponse> mapAuthorAndReactionData(List<Post> posts, Map<UUID, Map<String, Object>> batchReactionStats) {
        if (posts.isEmpty()) {
            return Collections.emptyList();
        }

        Set<UUID> authorIds = posts.stream().map(Post::getAuthorId).collect(Collectors.toSet());
        Map<UUID, Author> authorsMap = authorService.findAllAuthors(authorIds).stream()
                .collect(Collectors.toMap(Author::getUserId, author -> author));

        return posts.stream().map(post -> {
            PostResponse response = postMapper.toPostResponse(
                    post,
                    authorsMap.getOrDefault(post.getAuthorId(), authorService.getFallbackAuthor(post.getAuthorId()))
            );

            Map<String, Object> reactionStats = batchReactionStats.get(post.getId());
            if (reactionStats != null && !reactionStats.isEmpty()) {
                // TRƯỜNG HỢP 1: CACHE HIT (Xài dữ liệu từ Redis)
                List<ReactionTypeEnum> top3Reactions = reactionStats.entrySet().stream()
                        .filter(entry -> !entry.getKey().equals(ReactionCacheService.TOTAL_STATS_REDIS_KEY))
                        .sorted((e1, e2) -> {
                            long count1 = Long.parseLong(String.valueOf(e1.getValue()));
                            long count2 = Long.parseLong(String.valueOf(e2.getValue()));
                            return Long.compare(count2, count1);
                        })
                        .limit(3)
                        .map(entry -> ReactionTypeEnum.valueOf(entry.getKey()))
                        .toList();
                response.setTopReactions(top3Reactions);

                if (reactionStats.containsKey(ReactionCacheService.TOTAL_STATS_REDIS_KEY)) {
                    response.setReactionCounts(Integer.parseInt(String.valueOf(reactionStats.get(ReactionCacheService.TOTAL_STATS_REDIS_KEY))));
                }
            } else {
                var top3ReactionsInDB = post.getReactionStats();
                if (top3ReactionsInDB != null && !top3ReactionsInDB.isEmpty()) {
                    List<ReactionTypeEnum> top3Reactions = top3ReactionsInDB.entrySet().stream()
                            .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                            .limit(3)
                            .map(entry -> ReactionTypeEnum.valueOf(entry.getKey()))
                            .toList();
                    response.setTopReactions(top3Reactions);
                } else {
                    response.setTopReactions(Collections.emptyList());
                }
            }

            return response;
        }).toList();
    }
}
