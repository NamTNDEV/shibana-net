package com.shibana.post_service.model.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.shibana.post_service.model.embedded.Media;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_comments_post_id", columnList = "post_id"),
                @Index(name = "idx_comments_author_id", columnList = "author_id")
        }
)
public class Comment {
    @Id
    @Builder.Default
    UUID id = UuidCreator.getTimeOrderedEpoch();

    @Column(name = "post_id", nullable = false, updatable = false)
    UUID postId;

    @Column(name = "author_id", nullable = false, updatable = false)
    UUID authorId;

    @Column(columnDefinition = "TEXT")
    String content;

    @Column(columnDefinition = "ltree")
    String path;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    Media media = null;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    Map<String, Integer> reactionStats = new HashMap<>();

    @Builder.Default
    Integer replyCount = 0;

    @Builder.Default
    Boolean isEdited = false;

    @Builder.Default
    Boolean isDeleted = false;

    @CreationTimestamp
    @Column(updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    @Version
    Integer version;
}
