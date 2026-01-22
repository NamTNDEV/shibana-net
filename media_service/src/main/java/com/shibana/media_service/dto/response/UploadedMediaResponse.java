package com.shibana.media_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadedMediaResponse {
    String mediaId;
    String fileName;
    String url;
    String storageType;
}
