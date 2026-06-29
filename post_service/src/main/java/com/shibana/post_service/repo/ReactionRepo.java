package com.shibana.post_service.repo;

import com.shibana.post_service.model.entity.Reaction;
import com.shibana.post_service.repo.projection.ReactionCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReactionRepo extends JpaRepository<Reaction, UUID> {
    Optional<List<Reaction>> findAllByTargetId(UUID targetId);
    Optional<Reaction> findByTargetIdAndAuthorId(UUID targetId, UUID authorId);

    @Query("""
            SELECT r.reactionType AS reactionType, COUNT(r.id) AS count
            FROM Reaction r
            WHERE r.targetId = :targetId
            GROUP BY r.reactionType
            """)
    List<ReactionCountProjection> countReactionsByTargetId(@Param("targetId") UUID targetId);
}
