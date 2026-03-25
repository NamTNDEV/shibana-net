package com.shibana.post_service.model.entity;

import com.shibana.post_service.model.embedded.Author;
import com.shibana.post_service.model.embedded.Media;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "comments")
//@CompoundIndex(name = "post_parent_created_idx", def = "{'postId': 1, 'parentCommentId': 1, createdAt: 1}")
public class Comment {
    @Id
    @Builder.Default
    String id = UUID.randomUUID().toString();

    String postId;
    Author author;
    String content;
    String parentCommentId;

    @Builder.Default
    List<Media> media =  new ArrayList<>();

    @Builder.Default
    Map<String, Integer> reactionStatus = new HashMap<>();

    @Builder.Default
    Integer replyCount = 0;

    @CreatedDate
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;
}
