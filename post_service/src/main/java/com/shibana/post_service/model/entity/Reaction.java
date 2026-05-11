package com.shibana.post_service.model.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.shibana.post_service.model.enums.ReactionTargetTypeEnum;
import com.shibana.post_service.model.enums.ReactionTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        name = "reactions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reaction_target_author",
                        columnNames = {"target_id", "author_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_reaction_target_type",
                        columnList = "target_id, reaction_type"
                )
        }
)
public class Reaction {
    @Id
    @Builder.Default
    UUID id = UuidCreator.getTimeOrderedEpoch();

    @Column(name = "target_id", nullable = false, updatable = false)
    UUID targetId;

    @Column(name = "author_id", nullable = false, updatable = false)
    UUID authorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    ReactionTypeEnum reactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false,  updatable = false)
    ReactionTargetTypeEnum targetType;

    @CreationTimestamp
    @Column(updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    @Version
    Long version;
}
