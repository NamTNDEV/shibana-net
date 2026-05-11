package com.shibana.post_service.repo;

import com.shibana.post_service.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepo extends JpaRepository<Post, UUID> {
}
