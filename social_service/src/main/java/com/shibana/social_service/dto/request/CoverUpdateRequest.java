package com.shibana.social_service.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CoverUpdateRequest {
    String coverMediaName;

    @NotNull(message = "COVER_POSITION_Y_REQUIRED")
    @Min(value = 0, message = "INVALID_COVER_POSITION")
    @Max(value = 100, message = "INVALID_COVER_POSITION")
    Double coverPositionY;
}
