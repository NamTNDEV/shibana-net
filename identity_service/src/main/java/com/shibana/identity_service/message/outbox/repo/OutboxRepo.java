package com.shibana.identity_service.message.outbox.repo;

import com.shibana.identity_service.message.outbox.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxRepo extends JpaRepository<OutboxEvent, UUID> {
}
