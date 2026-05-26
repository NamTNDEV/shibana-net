package com.shibana.post_service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table(name = "cached_users")
public class Author {
    @Id
    @Column(name = "id")
    UUID userId;

    @Column(nullable = false)
    String username;

    @Column(name = "display_name")
    String displayName;

    @Column(name = "avatar_media_name")
    String avatarMediaName;

    @Column(name = "avatar_scale")
    Double avatarScale;

    @Column(name = "avatar_position_x")
    Double avatarPositionX;

    @Column(name = "avatar_position_y")
    Double avatarPositionY;

    @Column(name = "created_at", updatable = false, nullable = false)
    Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    Instant updatedAt;
}
