package com.shibana.post_service.model.entity;

import com.shibana.post_service.model.embedded.Author;
import com.shibana.post_service.model.embedded.Media;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "comments")
@CompoundIndex(name = "postId_path_createdAt_idx", def = "{'postId': 1, 'path': 1, 'createdAt': -1}")
public class Comment {
    @Id
    @Builder.Default
    String id = UUID.randomUUID().toString();

    String postId;
    Author author;
    String content;
    String path;

    @Builder.Default
    Media media = null;

    @Builder.Default
    Map<String, Integer> reactionStats = new HashMap<>();

    @Builder.Default
    Integer replyCount = 0;

    @Builder.Default
    Integer level = 0;

    @CreatedDate
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;

    @Version
    Integer version;
}
