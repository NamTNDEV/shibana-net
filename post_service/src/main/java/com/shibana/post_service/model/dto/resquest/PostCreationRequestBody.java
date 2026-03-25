package com.shibana.post_service.model.dto.resquest;

import com.shibana.post_service.model.enums.PostPrivacyEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class PostCreationRequestBody {
    @NotBlank(message = "CONTENT_REQUIRED")
    @Size(max = 1000, min = 1, message = "INVALID_CONTENT_LENGTH")
    String content;

    @NotNull(message = "INVALID_PRIVACY")
    PostPrivacyEnum privacy;
}
