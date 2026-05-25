package com.shibana.social_service.message.outbox.repo;

import com.shibana.social_service.message.outbox.entity.OutboxEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepo extends Neo4jRepository<OutboxEvent, UUID> {
    @Query("""
            MATCH (e:outbox_events)
            WHERE e.status = 'FAILED'
            AND e.retryCount <= $maxRetries
            RETURN e
            ORDER BY e.createdAt ASC;
            """)
    List<OutboxEvent> findFailedEvents(@Param("maxRetries") int maxRetries, Pageable pageable);

    @Query("""
            MATCH (e:outbox_events)
            WHERE e.status = 'PENDING'
            AND e.createdAt < $pendingTimeout
            RETURN e
            ORDER BY e.createdAt ASC;
            """)
    List<OutboxEvent> findPendingEvents(@Param("pendingTimeout") Instant pendingTimeout, Pageable pageable);

    @Query("""
            MATCH (e:outbox_events)
            WHERE e.status = 'COMPLETED'
            AND e.createdAt < $threshold DETACH DELETE e
            """)
    void deleteOldCompletedEvents(@Param("threshold") Instant threshold);
}
