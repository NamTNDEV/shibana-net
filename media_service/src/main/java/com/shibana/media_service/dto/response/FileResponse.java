package com.shibana.media_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileResponse {
    Resource file;
    String contentType;
}
