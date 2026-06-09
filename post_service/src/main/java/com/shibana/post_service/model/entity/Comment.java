package com.shibana.post_service.model.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.shibana.post_service.model.embedded.Media;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnTransformer;
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
                // Phục vụ Root: Sắp xếp DESC
                @Index(name = "idx_comments_root_query", columnList = "post_id, is_delete, level, id DESC"),
                // Phục vụ Reply: Sắp xếp ASC
                @Index(name = "idx_comments_reply_query", columnList = "parent_id, is_delete, id ASC")
//                @Index(name = "idx_comments_author_feeds", columnList = "author_id, created_at DESC")
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
    @ColumnTransformer(write = "?::ltree")
    String path;

    @Column(name = "parent_id")
    UUID parentId;

    @Column(nullable = false)
    @Builder.Default
    Integer level = 0;

    @Column(name = "media_url")
    String mediaUrl;

    @Column(name = "media_type", length = 20)
    String mediaType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "reaction_stats")
    @Builder.Default
    Map<String, Integer> reactionStats = new HashMap<>();

    @Column(name = "reply_count", nullable = false)
    @Builder.Default
    Integer replyCount = 0;

    @Column(name = "is_edited", nullable = false)
    @Builder.Default
    Boolean isEdited = false;

    @Column(name = "is_delete", nullable = false)
    @Builder.Default
    Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    Instant updatedAt;

    @Version
    Integer version;
}
