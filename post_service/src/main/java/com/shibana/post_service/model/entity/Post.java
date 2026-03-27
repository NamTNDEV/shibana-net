package com.shibana.post_service.model.entity;

import com.shibana.post_service.model.embedded.Author;
import com.shibana.post_service.model.embedded.Media;
import com.shibana.post_service.model.embedded.Mention;
import com.shibana.post_service.model.enums.PostPrivacyEnum;
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
@CompoundIndexes({
        @CompoundIndex(name = "author_created_privacy_idx", def = "{'author.id': 1, 'createdAt': -1, 'privacy': 1}"),
        @CompoundIndex(name = "privacy_created_idx", def = "{'privacy': 1, 'createdAt': -1}"),
        @CompoundIndex(name = "hashtag_created_privacy_idx", def = "{'hashtags': 1, 'createdAt': -1, 'privacy': 1}"),
})
@Document(collection = "posts")
public class Post {
    @Id
    @Builder.Default
    String id = UUID.randomUUID().toString();

    Author author;
    String content;
    PostPrivacyEnum privacy;

    @Builder.Default
    List<String> hashtags = new ArrayList<>();

    @Builder.Default
    List<Mention> mentions = new ArrayList<>();

    @Builder.Default
    private List<Media> media = new ArrayList<>();

    @Builder.Default
    Map<String, Integer> reactionStats = new HashMap<>();

    @Builder.Default
    Integer commentCount = 0;

    @CreatedDate
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;

    @Version
    Integer version;
}
