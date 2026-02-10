package com.shibana.post_service.dto.resquest;

import com.shibana.post_service.exception.ErrorCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PostCreationRequest {
    @NotBlank(message = "CONTENT_REQUIRED")
    @Size(max = 5000, min = 1, message = "INVALID_CONTENT_LENGTH")
    String content;
}
