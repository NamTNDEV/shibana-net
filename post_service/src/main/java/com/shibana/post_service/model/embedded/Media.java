package com.shibana.post_service.model.embedded;

import com.shibana.post_service.model.enums.MediaTypeEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Media {
    String mediaName;
    MediaTypeEnum mediaType;
}
