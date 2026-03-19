package com.shibana.social_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FollowRequestBody {
    @NotBlank(message = "INVALID_FOLLOW_BODY")
    String followeeId;
}
