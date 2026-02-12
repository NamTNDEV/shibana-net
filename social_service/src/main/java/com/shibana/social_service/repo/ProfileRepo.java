package com.shibana.social_service.repo;

import com.shibana.social_service.dto.response.ProfileMetadataResponse;
import com.shibana.social_service.entity.Profile;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepo extends Neo4jRepository<Profile, String> {
    Optional<Profile> findByUserId(String userId);

    @Query("MATCH (p:user_profiles) WHERE p.username = $username RETURN p")
    Optional<Profile> findByUsername(@Param("username") String username);

    @Query("MATCH (p:user_profiles) WHERE p.userId = $userId " +
            "RETURN p.id as id, " +
            "p.firstName as firstName, " +
            "p.lastName as lastName, " +
            "coalesce(p.avatar, '') as avatar")
    Optional<Profile>  findProfileMetadata(@Param("userId") String userId);
}
