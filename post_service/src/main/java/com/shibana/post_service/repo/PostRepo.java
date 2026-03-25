package com.shibana.post_service.repo;

import com.shibana.post_service.model.entity.Post;
import com.shibana.post_service.model.enums.PostPrivacyEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepo extends MongoRepository<Post, String> {
    // --- Post ---
    Optional<Post> getPostById(String postId);

    // --- Feed ---
    Slice<Post> getPostsByAuthorUserIdAndPrivacyIn(String authorId, List<PostPrivacyEnum> allowedPrivacies, Pageable pageable);

    // --- Hashtag ---
    @Query("{ 'hashtags': ?0, 'privacy': ?1 }")
    Slice<Post> getPostByHashtagAndPrivacy(String hashtag, PostPrivacyEnum privacy, Pageable pageable);
}
