package com.shibana.profile_service.repo;

import com.shibana.profile_service.entity.Profile;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepo extends Neo4jRepository<Profile, String> {
}
