package com.shibana.post_service.model.dto.response;

import com.shibana.post_service.model.embedded.Author;
import com.shibana.post_service.model.enums.PostPrivacyEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PostResponse {
    String id;
    String content;
    Author author;
    PostPrivacyEnum privacy;
    Instant createdAt;
    Integer commentCount;
}
