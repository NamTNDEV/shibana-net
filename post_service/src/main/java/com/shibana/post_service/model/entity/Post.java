package com.shibana.post_service.model.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.shibana.post_service.model.embedded.Media;
import com.shibana.post_service.model.embedded.Mention;
import com.shibana.post_service.model.enums.PostPrivacyEnum;
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
        name = "posts",
        indexes = {
                @Index(name = "idx_newsfeed", columnList = "author_id, privacy, id DESC"),
        }
)
public class Post {
    @Id
    @Builder.Default
    UUID id = UuidCreator.getTimeOrderedEpoch();

    @Column(name = "author_id", nullable = false, updatable = false)
    UUID authorId;

    @Column(columnDefinition = "TEXT")
    String content;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    PostPrivacyEnum privacy = PostPrivacyEnum.PUBLIC;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "TEXT[]")
    @Builder.Default
    List<String> hashtags = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    List<Mention> mentions = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private List<Media> media = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reaction_stats", columnDefinition = "jsonb")
    @Builder.Default
    Map<String, Integer> reactionStats = new HashMap<>();

    @Builder.Default
    @Column(name = "comment_counts")
    Integer commentCounts = 0;

    @Builder.Default
    @Column(name = "is_edited")
    Boolean isEdited = false;

    @Builder.Default
    @Column(name = "is_deleted")
    Boolean isDeleted = false;

    @Column(name = "created_at", updatable = false)
    Instant createdAt;

    @Column(name = "updated_at")
    Instant updatedAt;

    @Version
    Integer version;

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
