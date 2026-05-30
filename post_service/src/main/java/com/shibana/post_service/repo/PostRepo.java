package com.shibana.post_service.repo;

import com.shibana.post_service.model.entity.Post;
import com.shibana.post_service.model.enums.PostPrivacyEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PostRepo extends JpaRepository<Post, UUID> {
    @Query(value = """
            SELECT *
            FROM (
                (SELECT * FROM posts WHERE author_id = :requesterId AND id < :cursor ORDER BY id DESC LIMIT :limit)
                UNION ALL
                (
                    SELECT *
                    FROM posts
                    WHERE author_id IN (:friendIds)
                    AND privacy IN (:allowedFriendPrivacies)
                    AND id < :cursor
                    ORDER BY id DESC
                    LIMIT :limit
                )
                UNION ALL
                (
                    SELECT *
                    FROM posts
                    WHERE author_id IN (:followingIds)
                    AND privacy IN (:allowedFollowingPrivacies)
                    AND id < :cursor
                    ORDER BY id DESC
                    LIMIT :limit
                )
            ) AS combined_posts
            ORDER BY id DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Post> getNewsfeed(
            @Param("requesterId") UUID requesterId,
            @Param("friendIds") Set<UUID> friendIds, @Param("allowedFriendPrivacies") Set<String> allowedFriendPrivacies,
            @Param("followingIds") Set<UUID> followingIds, @Param("allowedFollowingPrivacies") Set<String> allowedFollowingPrivacies,
            @Param("cursor") UUID cursor,
            @Param("limit") int limit
    );

    @Query(value = """
            SELECT *
            FROM posts
            WHERE id NOT IN (:existingPostIds)
            AND privacy IN (:publicPrivacy)
            AND id < :cursor
            ORDER BY id DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Post> getFallbackNewsfeed(
            @Param("existingPostIds") Set<UUID> existingPostIds,
            @Param("publicPrivacy") String publicPrivacy,
            @Param("cursor") UUID cursor,
            @Param("limit") int limit
    );
}
