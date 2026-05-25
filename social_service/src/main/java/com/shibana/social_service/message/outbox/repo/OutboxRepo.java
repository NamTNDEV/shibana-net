package com.shibana.social_service.message.outbox.repo;

import com.shibana.social_service.message.outbox.entity.OutboxEvent;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OutboxRepo extends Neo4jRepository<OutboxEvent, UUID> {
}
