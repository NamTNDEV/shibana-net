package com.shibana.post_service.repo;

import com.shibana.post_service.model.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReactionRepo extends JpaRepository<Reaction, UUID> {
    Optional<List<Reaction>> findAllByTargetId(UUID targetId);
    Optional<Reaction> findByTargetIdAndAuthorId(UUID targetId, UUID authorId);
}
