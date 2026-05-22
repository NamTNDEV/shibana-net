package com.shibana.social_service.repo;

import com.shibana.social_service.entity.Profile;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepo extends Neo4jRepository<Profile, UUID> {
    Optional<Profile> findByUserId(UUID userId);

    @Query("""
            MATCH (target:profiles{username:$username})
            OPTIONAL MATCH (viewer:profiles{userId:$viewerId})
            
            WITH target, viewer
            
            WHERE viewer IS NULL
            OR viewer = target
            OR NOT EXISTS((viewer)-[:BLOCKS]-(target))
            
            RETURN target;
            """)
    Optional<Profile> findByUsername(@Param("username") String username, @Param("viewerId") UUID viewerId);

    @Query("MATCH (p:profiles) WHERE p.userId = $userId " +
            "RETURN p.id as id, " +
            "p.username as username, " +
            "p.firstName as firstName, " +
            "p.lastName as lastName, " +
            "p.avatarMediaName as avatarMediaName, " +
            "p.avatarScale as avatarScale, " +
            "p.avatarPositionX as avatarPositionX, " +
            "p.avatarPositionY as avatarPositionY")
    Optional<Profile> findProfileMetadata(@Param("userId") UUID userId);
}
