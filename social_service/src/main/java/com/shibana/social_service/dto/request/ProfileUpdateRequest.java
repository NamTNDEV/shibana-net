package com.shibana.social_service.dto.request;

import com.shibana.social_service.enums.PrivacyLevel;
import com.shibana.social_service.enums.ProfileField;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ProfileUpdateRequest {
    @NotNull(message = "INVALID_UPDATE_PRIVACY_LEVEL")
    PrivacyLevel  privacyLevel;

    @NotNull(message = "INVALID_UPDATE_PROFILE_FIELD")
    ProfileField fieldKey;

    String content;
}
