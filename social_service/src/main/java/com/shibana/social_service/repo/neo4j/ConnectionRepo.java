package com.shibana.social_service.repo.neo4j;

import com.shibana.social_service.dto.response.ConnectionStatusResponse;
import com.shibana.social_service.entity.Profile;
import com.shibana.social_service.enums.friendship_status.FriendResponseEligibilityStatus;
import com.shibana.social_service.enums.friendship_status.FriendRequestEligibilityStatus;
import com.shibana.social_service.enums.friendship_status.RevokeRequestEligibilityStatus;
import com.shibana.social_service.enums.friendship_status.UnfriendEligibilityStatus;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionRepo extends Neo4jRepository<Profile, String> {
    // --- Follow ---
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

    // --- Friendship ---
    @Query("""
            MATCH (sender:user_profiles {userId:$senderId})
            OPTIONAL MATCH (reciever:user_profiles {userId:$recieverId})
            RETURN
              CASE
                WHEN reciever IS NULL THEN "PROFILE_NOT_FOUND"
                WHEN EXISTS((sender)-[:REQUESTS_FRIEND]->(reciever)) THEN "SENT_REQUEST"
                WHEN EXISTS((reciever)-[:REQUESTS_FRIEND]->(sender)) THEN "RECEIVED_REQUEST"
                WHEN EXISTS {
                      MATCH (reciever)-[rej:REJECTS]->(sender)
                      WHERE rej.createdAt > datetime() - duration('P30D')
                    } THEN 'BE_REJECTED'
                WHEN EXISTS((sender)-[:FRIENDS]->(reciever)) THEN "FRIENDED"
                ELSE "READY"
              END;
            """)
    FriendRequestEligibilityStatus checkFriendRequestEligibility(@Param("senderId") String senderId, @Param("recieverId") String recieverId);

    @Query("""
            MATCH (acceptor:user_profiles{userId:$acceptorId})
            OPTIONAL MATCH (requester:user_profiles{userId:$requesterId})
            RETURN
              CASE
                WHEN requester IS NULL THEN 'PROFILE_NOT_FOUND'
                WHEN EXISTS((requester)-[:REQUESTS_FRIEND]->(acceptor)) THEN 'READY'
                WHEN EXISTS((requester)-[:FRIENDS]-(acceptor)) THEN 'FRIENDED'
                ELSE 'NO_SEND_REQUEST'
              END
            """)
    FriendResponseEligibilityStatus checkFriendResponseEligibility(@Param("acceptorId") String acceptorId, @Param("requesterId") String requesterId);

    @Query("""
            MATCH (revoker:user_profiles{userId:$revokerId})
                  OPTIONAL MATCH (revokee:user_profiles{userId:$revokeeId})
                  RETURN
                    CASE
                      WHEN revokee IS NULL THEN 'PROFILE_NOT_FOUND'
                      WHEN EXISTS((revoker)-[:REQUESTS_FRIEND]->(revokee)) THEN 'READY'
                      ELSE 'NO_SEND_REQUEST'
                    END;
            """)
    RevokeRequestEligibilityStatus checkRevokeFriendResponseEligibility(@Param("revokerId") String revokerId, @Param("revokeeId") String revokeeId);

    @Query("""
            MATCH (unfriender:user_profiles{userId:$unfrienderId})
            OPTIONAL MATCH (unfriendee:user_profiles{userId:$unfriendeeId})
            RETURN
              CASE
                WHEN unfriendee IS NULL THEN 'PROFILE_NOT_FOUND'
                WHEN (unfriender)-[:FRIENDS]-(unfriendee) THEN 'FRIENDED'
                ELSE 'NOT_FRIENDED'
              END
            """)
    UnfriendEligibilityStatus checkUnfriendEligibility(@Param("unfrienderId") String unfrienderId, @Param("unfriendeeId") String unfriendeeId);

    @Query("""
            MATCH (sender:user_profiles{userId:$senderId})
            MATCH (reciever:user_profiles{userId:$recieverId})
            
            OPTIONAL MATCH (sender)-[rej:REJECTS]->(reciever)
            DELETE rej
            
            MERGE (sender)-[fo:FOLLOWS]->(reciever)
            ON CREATE SET fo.createdAt = datetime()
            
            MERGE (sender)-[fr:REQUESTS_FRIEND]->(reciever)
            ON CREATE SET fr.createdAt = datetime()
            """)
    void sendFriendRequest(@Param("senderId") String senderId, @Param("recieverId") String recieverId);

    @Query("""
            MATCH (acceptor:user_profiles{userId:$acceptorId})
            MATCH (requester:user_profiles{userId:$requesterId})
            OPTIONAL MATCH (acceptor)-[rf:REQUESTS_FRIEND]-(requester)
            DELETE rf
            MERGE (acceptor)-[f:FRIENDS]->(requester)
            ON CREATE SET f.createdAt = datetime()
            MERGE (acceptor)-[fl:FOLLOWS]->(requester)
            ON CREATE SET fl.createdAt = datetime()
            MERGE (requester)-[fl_rev:FOLLOWS]->(acceptor)
            ON CREATE SET fl_rev.createdAt = datetime()
            """)
    void acceptFriendRequest(@Param("acceptorId") String acceptorId, @Param("requesterId") String requesterId);

    @Query("""
            MATCH (rejector:user_profiles{userId:$rejectorId})
            MATCH (requester:user_profiles{userId:$requesterId})
            
            MATCH (requester)-[rf:REQUESTS_FRIEND]->(rejector)
            DELETE rf
            
            MERGE (rejector)-[rj:REJECTS]->(requester)
            ON CREATE SET rj.createdAt = datetime()
            """)
    void rejectFriendRequest(@Param("rejectorId") String rejectorId, @Param("requesterId") String requesterId);

    @Query("""
            OPTIONAL MATCH (:user_profiles{userId:$unfrienderId})-[r:FRIENDS|FOLLOWS|REQUESTS_FRIEND|REJECTS]-(:user_profiles{userId:$unfriendeeId})
            DELETE r;
            """)
    void unfriend(@Param("unfrienderId") String unfrienderId, @Param("unfriendeeId") String unfriendeeId);

    @Query("""
            MATCH (revoker:user_profiles{userId:$revokerId})
            MATCH (revokee:user_profiles{userId:$revokeeId})
            OPTIONAL MATCH (revoker)-[r:REQUESTS_FRIEND|FOLLOWS]->(revokee)
            DELETE r
            """)
    void revokeRequest(@Param("revokerId") String revokerId, @Param("revokeeId") String revokeeId);

    // --- Other ---
    @Query("""
            MATCH (targeter:user_profiles{userId:$targetId})
            OPTIONAL MATCH (viewer:user_profiles{userId:$viewerId})
            RETURN
                CASE
                  WHEN viewer IS NULL THEN 'NONE'
                  WHEN EXISTS((viewer)-[:FRIENDS]-(targeter)) THEN 'FRIENDED'
                  WHEN EXISTS((viewer)-[:REQUESTS_FRIEND]->(targeter)) THEN 'SENT_REQUEST'
                  WHEN EXISTS((targeter)-[:REQUESTS_FRIEND]->(viewer)) THEN 'RECEIVED_REQUEST'
                  WHEN EXISTS {
                    MATCH (targeter)-[rej:REJECTS]->(viewer)
                    WHERE rej.createdAt > datetime() - duration('P30D')
                    } THEN 'BE_REJECTED'
                  ELSE 'NONE'
                END AS friendshipStatus,
            
                CASE
                  WHEN viewer IS NULL THEN false
                  WHEN EXISTS((viewer)-[:FOLLOWS]->(targeter)) THEN true
                  ELSE false
                END AS isFollowing
            """)
    ConnectionStatusResponse getConnectionStatus(@Param("viewerId") String viewerId, @Param("targetId") String targetId);
}
