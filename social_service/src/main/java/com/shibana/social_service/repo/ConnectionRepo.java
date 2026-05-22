package com.shibana.social_service.repo;

import com.shibana.social_service.dto.response.ConnectionStatusResponse;
import com.shibana.social_service.dto.response.NewsfeedTargetResponse;
import com.shibana.social_service.entity.Profile;
import com.shibana.social_service.enums.block_status.BlockEligibilityStatus;
import com.shibana.social_service.enums.friendship_status.FriendResponseEligibilityStatus;
import com.shibana.social_service.enums.friendship_status.FriendRequestEligibilityStatus;
import com.shibana.social_service.enums.friendship_status.RevokeRequestEligibilityStatus;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConnectionRepo extends Neo4jRepository<Profile, UUID> {
    // --- Follow ---
    @Query("MATCH (follower:profiles {userId: $followerId})" +
            "MATCH (followee:profiles {userId: $followeeId})" +
            "MERGE (follower)-[r:FOLLOWS]->(followee)" +
            "ON CREATE SET r.createdAt = datetime()" +
            "RETURN count(r) > 0;")
    boolean follow(@Param("followerId") UUID followerId, @Param("followeeId") UUID followeeId);

    @Query("MATCH (follower:profiles {userId: $followerId})-[r:FOLLOWS]->(followee:profiles {userId: $followeeId})" +
            "DELETE r;")
    void unfollow(@Param("followerId") UUID followerId, @Param("followeeId") UUID followeeId);

    @Query("RETURN EXISTS((:profiles {userId: $followerId})-[:FOLLOWS]->(:profiles {userId: $followeeId}))")
    boolean checkIsFollowing(@Param("followerId") UUID followerId, @Param("followeeId") UUID followeeId);

    // --- Friendship ---
    @Query("""
            MATCH (sender:profiles {userId:$senderId})
            OPTIONAL MATCH (reciever:profiles {userId:$recieverId})
            RETURN
              CASE
                WHEN reciever IS NULL THEN "PROFILE_NOT_FOUND"
                WHEN EXISTS((reciever)-[:BLOCKS]->(sender)) THEN "BE_BLOCKED"
                WHEN EXISTS((sender)-[:REQUESTS_FRIEND]->(reciever)) THEN "SENT_REQUEST"
                WHEN EXISTS((reciever)-[:REQUESTS_FRIEND]->(sender)) THEN "RECEIVED_REQUEST"
                WHEN EXISTS {
                      MATCH (reciever)-[rej:REJECTS]->(sender)
                      WHERE rej.createdAt > datetime() - duration('P30D')
                    } THEN 'BE_REJECTED'
                WHEN EXISTS((sender)-[:FRIENDS]-(reciever)) THEN "FRIENDED"
                ELSE "READY"
              END;
            """)
    FriendRequestEligibilityStatus checkFriendRequestEligibility(@Param("senderId") UUID senderId, @Param("recieverId") UUID recieverId);

    @Query("""
            MATCH (acceptor:profiles{userId:$acceptorId})
            OPTIONAL MATCH (requester:profiles{userId:$requesterId})
            RETURN
              CASE
                WHEN requester IS NULL THEN 'PROFILE_NOT_FOUND'
                WHEN EXISTS((requester)-[:REQUESTS_FRIEND]->(acceptor)) THEN 'READY'
                WHEN EXISTS((requester)-[:FRIENDS]-(acceptor)) THEN 'FRIENDED'
                ELSE 'NO_SEND_REQUEST'
              END
            """)
    FriendResponseEligibilityStatus checkFriendResponseEligibility(@Param("acceptorId") UUID acceptorId, @Param("requesterId") UUID requesterId);

    @Query("""
            MATCH (revoker:profiles{userId:$revokerId})
                  OPTIONAL MATCH (revokee:profiles{userId:$revokeeId})
                  RETURN
                    CASE
                      WHEN revokee IS NULL THEN 'PROFILE_NOT_FOUND'
                      WHEN EXISTS((revoker)-[:REQUESTS_FRIEND]->(revokee)) THEN 'READY'
                      ELSE 'NO_SEND_REQUEST'
                    END;
            """)
    RevokeRequestEligibilityStatus checkRevokeFriendResponseEligibility(@Param("revokerId") UUID revokerId, @Param("revokeeId") UUID revokeeId);

    @Query("""
            MATCH (sender:profiles{userId:$senderId})
            MATCH (reciever:profiles{userId:$recieverId})
            
            OPTIONAL MATCH (sender)-[rej:REJECTS]->(reciever)
            DELETE rej
            
            MERGE (sender)-[fo:FOLLOWS]->(reciever)
            ON CREATE SET fo.createdAt = datetime()
            
            MERGE (sender)-[fr:REQUESTS_FRIEND]->(reciever)
            ON CREATE SET fr.createdAt = datetime()
            """)
    void sendFriendRequest(@Param("senderId") UUID senderId, @Param("recieverId") UUID recieverId);

    @Query("""
            MATCH (acceptor:profiles{userId:$acceptorId})
            MATCH (requester:profiles{userId:$requesterId})
            OPTIONAL MATCH (acceptor)-[rf:REQUESTS_FRIEND]-(requester)
            DELETE rf
            MERGE (acceptor)-[f:FRIENDS]->(requester)
            ON CREATE SET f.createdAt = datetime()
            MERGE (acceptor)-[fl:FOLLOWS]->(requester)
            ON CREATE SET fl.createdAt = datetime()
            MERGE (requester)-[fl_rev:FOLLOWS]->(acceptor)
            ON CREATE SET fl_rev.createdAt = datetime()
            """)
    void acceptFriendRequest(@Param("acceptorId") UUID acceptorId, @Param("requesterId") UUID requesterId);

    @Query("""
            MATCH (rejector:profiles{userId:$rejectorId})
            MATCH (requester:profiles{userId:$requesterId})
            
            MATCH (requester)-[rf:REQUESTS_FRIEND]->(rejector)
            DELETE rf
            
            MERGE (rejector)-[rj:REJECTS]->(requester)
            ON CREATE SET rj.createdAt = datetime()
            """)
    void rejectFriendRequest(@Param("rejectorId") UUID rejectorId, @Param("requesterId") UUID requesterId);

    @Query("""
            MATCH (:profiles{userId:$unfrienderId})-[r:FRIENDS|FOLLOWS|REQUESTS_FRIEND|REJECTS]-(:profiles{userId:$unfriendeeId})
            DELETE r;
            """)
    void unfriend(@Param("unfrienderId") UUID unfrienderId, @Param("unfriendeeId") UUID unfriendeeId);

    @Query("""
            MATCH (revoker:profiles{userId:$revokerId})
            MATCH (revokee:profiles{userId:$revokeeId})
            OPTIONAL MATCH (revoker)-[r:REQUESTS_FRIEND|FOLLOWS]->(revokee)
            DELETE r
            """)
    void revokeRequest(@Param("revokerId") UUID revokerId, @Param("revokeeId") UUID revokeeId);

    // --- Block ---
    @Query("""
            MATCH (blocker:profiles{userId:$blockerId})
            OPTIONAL MATCH (blockee:profiles{userId:$blockeeId})
            RETURN
              CASE
                WHEN blockee IS NULL THEN 'PROFILE_NOT_FOUND'
                WHEN EXISTS((blocker)-[:BLOCKS]->(blockee)) THEN 'ALREADY_BLOCKED'
                WHEN EXISTS((blockee)-[:BLOCKS]->(blocker)) THEN 'BE_BLOCKED'
                ELSE 'READY'
              END;
            """)
    BlockEligibilityStatus checkBlockingEligibility(@Param("blockerId") UUID blockerId, @Param("blockeeId") UUID blockeeId);

    @Query("""
            MATCH (blocker:profiles{userId:$blockerId})
            MATCH (blockee:profiles{userId:$blockeeId})
            OPTIONAL MATCH (blocker)-[r:FRIENDS|FOLLOWS|REQUESTS_FRIEND|REJECTS]-(blockee)
            DELETE r
            WITH DISTINCT blocker, blockee
            MERGE (blocker)-[rb:BLOCKS]->(blockee)
            ON CREATE SET rb.createdAt = datetime();
            """)
    void blocks(@Param("blockerId") UUID blockerId, @Param("blockeeId") UUID blockeeId);

    @Query("""
            MATCH (:profiles{userId:$unblockerId})-[rb:BLOCKS]->(:profiles{userId:$unblockeeId})
            DELETE rb;
            """)
    void unblocks(@Param("unblockerId") UUID blockerId, @Param("unblockeeId") UUID blockeeId);

    // --- Other ---
    @Query("""
            MATCH (targeter:profiles{userId:$targetId})
            OPTIONAL MATCH (viewer:profiles{userId:$viewerId})
            RETURN
                CASE
                  WHEN viewer IS NULL THEN 'NONE'
                  WHEN EXISTS((targeter)-[:BLOCKS]-(viewer)) THEN 'BE_BLOCKED'
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
    ConnectionStatusResponse getConnectionStatus(@Param("viewerId") UUID viewerId, @Param("targetId") UUID targetId);

    @Query("""
            MATCH (me:profiles{userId:$requesterId})
            WITH me, [(me)-[:FRIENDS]-(f) | f.userId] as friendIds
            RETURN friendIds,
            [(me)-[:FOLLOWS]->(fl) WHERE NOT (me)-[:FRIENDS]-(fl) | fl.userId] AS followingIds;
            """)
    NewsfeedTargetResponse getNewsfeedTargeters(@Param("requesterId") UUID requesterId);
}
