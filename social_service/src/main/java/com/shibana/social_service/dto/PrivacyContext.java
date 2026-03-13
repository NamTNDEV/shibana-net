package com.shibana.social_service.dto;

public record PrivacyContext(
        boolean isOwner,
        boolean isFriends
) {
}
