package com.shibana.post_service.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Author {
    String userId;
    String username;
    String displayName;

    String avatarMediaName;
    Double avatarScale;
    Double avatarPositionX;
    Double avatarPositionY;
}
