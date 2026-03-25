package com.shibana.post_service.model.entity;

import com.shibana.post_service.model.enums.ReactionTargetTypeEnum;
import com.shibana.post_service.model.enums.ReactionTypeEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "reactions")
//@CompoundIndexes({
//        @CompoundIndex(name = "target_author_idx", def = "{'targetId': 1, 'authorId': 1}", unique = true),
//        @CompoundIndex(name = "target_type_created_idx", def = "{'targetId': 1, 'targetType': 1, 'createdAt': -1}")
//})

public class Reaction {
    @Id
    @Builder.Default
    String id = UUID.randomUUID().toString();

    String targetId;
    String authorId;

    ReactionTypeEnum type;
    ReactionTargetTypeEnum  targetType;

    @CreatedDate
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;
}
