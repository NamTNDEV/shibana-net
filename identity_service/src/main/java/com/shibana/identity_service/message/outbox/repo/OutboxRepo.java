package com.shibana.identity_service.message.outbox.repo;

import com.shibana.identity_service.message.outbox.entity.OutboxEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepo extends JpaRepository<OutboxEvent, UUID> {
    @Query("SELECT e FROM OutboxEvent e WHERE e.status = 'FAILED' AND e.retryCount <= :maxRetries ORDER BY e.createdAt ASC")
    List<OutboxEvent> findFailedEvents(@Param("maxRetries") int maxRetries, Pageable pageable);

    @Query("SELECT e FROM OutboxEvent e WHERE e.status = 'PENDING' AND e.createdAt <= :timeout ORDER BY e.createdAt ASC")
    List<OutboxEvent> findStuckPendingEvent(@Param("timeout") Instant timeout, Pageable pageable);
}