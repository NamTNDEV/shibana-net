package com.shibana.social_service.repo.neo4j;

import com.shibana.social_service.entity.Profile;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionRepo extends Neo4jRepository<Profile, String> {
    @Query("MATCH (follower:user_profiles {userId: $followerId})" +
            "MATCH (followee:user_profiles {userId: $followeeId})" +
            "MERGE (follower)-[r:FOLLOWS]->(followee)" +
            "ON CREATE SET r.createdAt = datetime()" +
            "RETURN count(r) > 0;")
    boolean follow(@Param("followerId") String followerId, @Param("followeeId") String followeeId);

    @Query("MATCH (follower:user_profiles {userId: $followerId})-[r:FOLLOWS]->(followee:user_profiles {userId: $followeeId})" +
            "DELETE r;")
    void unfollow(@Param("followerId") String followerId, @Param("followeeId") String followeeId);

    @Query("RETURN EXISTS((:user_profiles {userId: $followerId})-[:FOLLOWS]->(:user_profiles {userId: $followeeId}))")
    boolean checkIsFollowing(@Param("followerId") String followerId, @Param("followeeId") String followeeId);
}
