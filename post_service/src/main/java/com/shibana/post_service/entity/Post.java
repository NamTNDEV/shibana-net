package com.shibana.post_service.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "posts")
@CompoundIndexes({
        @CompoundIndex(
                name = "author_createdAt_idx",
                def = "{'authorId': 1, 'createdAt': -1}"
        )
})
public class Post {
    @Id
    String id;

    @Indexed
    String authorId;

    String content;

    @Indexed(direction = IndexDirection.DESCENDING)
    @CreatedDate
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;
}
