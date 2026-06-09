package com.shibana.post_service.model.dto.resquest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RootCommentCreationRequestBody {
    @NotBlank(message = "INVALID_CONTENT_LENGHT")
    @Size(max = 500, message = "INVALID_CONTENT_LENGHT")
    String content;
}
