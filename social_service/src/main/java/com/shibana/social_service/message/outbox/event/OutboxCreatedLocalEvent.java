package com.shibana.social_service.message.outbox.event;

import java.util.UUID;

public record OutboxCreatedLocalEvent(UUID outboxId) {
}
