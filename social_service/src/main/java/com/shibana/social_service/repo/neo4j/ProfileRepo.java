package com.shibana.social_service.repo.neo4j;

import com.shibana.social_service.entity.Profile;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepo extends Neo4jRepository<Profile, String> {
    Optional<Profile> findByUserId(String userId);

    @Query("""
            MATCH (target:user_profiles{username:$username})
            OPTIONAL MATCH (viewer:user_profiles{userId:$viewerId})
            
            WITH target, viewer
            
            WHERE viewer IS NULL
            OR viewer = target
            OR NOT EXISTS((viewer)-[:BLOCKS]-(target))
            
            RETURN target;
            """)
    Optional<Profile> findByUsername(@Param("username") String username, @Param("viewerId") String viewerId);

    @Query("MATCH (p:user_profiles) WHERE p.userId = $userId " +
            "RETURN p.id as id, " +
            "p.username as username, " +
            "p.firstName as firstName, " +
            "p.lastName as lastName, " +
            "p.avatarMediaName as avatarMediaName, " +
            "p.avatarScale as avatarScale, " +
            "p.avatarPositionX as avatarPositionX, " +
            "p.avatarPositionY as avatarPositionY")
    Optional<Profile> findProfileMetadata(@Param("userId") String userId);
}
