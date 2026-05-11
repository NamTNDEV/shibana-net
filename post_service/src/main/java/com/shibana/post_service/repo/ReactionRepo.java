package com.shibana.post_service.repo;

import com.shibana.post_service.model.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReactionRepo extends JpaRepository<Reaction, UUID> {
}
