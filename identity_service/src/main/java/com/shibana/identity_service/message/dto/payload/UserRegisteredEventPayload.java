package com.shibana.identity_service.message.dto.payload;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record UserRegisteredEventPayload(
        UUID userId,
        String firstName,
        String lastName,
        LocalDate dob,
        String username,
        String email,
        Instant createdAt
) {
}
