package com.shibana.post_service.repo;

import com.shibana.post_service.model.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepo extends MongoRepository<Post, String> {
    Optional<Post> getPostById(String postId);
}
