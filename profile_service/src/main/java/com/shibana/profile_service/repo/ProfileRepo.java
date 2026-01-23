package com.shibana.profile_service.repo;

import com.shibana.profile_service.entity.Profile;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepo extends Neo4jRepository<Profile, String> {
    Optional<Profile> findByUserId(String userId);
}
