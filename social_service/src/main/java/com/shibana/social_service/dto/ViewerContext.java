package com.shibana.social_service.dto;

public record ViewerContext(
        boolean isOwner,
        RelationshipContext relationshipContext
) {
}
