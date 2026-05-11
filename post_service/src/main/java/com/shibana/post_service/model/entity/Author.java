package com.shibana.post_service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    UUID userId;

    @Column(nullable = false)
    String username;

    String displayName;

    String avatarMediaName;
    Double avatarScale;
    Double avatarPositionX;
    Double avatarPositionY;
}
