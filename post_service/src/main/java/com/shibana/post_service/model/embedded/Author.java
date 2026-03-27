package com.shibana.post_service.model.embedded;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@ToString
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
