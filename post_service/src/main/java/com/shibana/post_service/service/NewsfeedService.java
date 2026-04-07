package com.shibana.post_service.service;

import com.shibana.post_service.http_client.SocialClient;
import com.shibana.post_service.mapper.PostMapper;
import com.shibana.post_service.model.dto.response.PageResponse;
import com.shibana.post_service.model.dto.response.PostResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsfeedService {
    PostRepo postRepo;
    PostMapper postMapper;
    SocialClient socialClient;

    public PageResponse<PostResponse> getNewsfeed(String requesterId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        var targetData = socialClient.getNewsfeedTargertersId(requesterId).getData();
        Set<String> friendIds = targetData.friendIds() != null ? targetData.friendIds() : Set.of();
        Set<String> followingIds = targetData.followingIds() != null ? targetData.followingIds() : Set.of();

        List<PostPrivacyEnum> allowedFriendPrivacies = List.of(PostPrivacyEnum.FRIENDS, PostPrivacyEnum.PUBLIC);
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
}
