package com.shibana.social_service.dto.request;

import com.shibana.social_service.enums.profile_privacy_status.PrivacyLevel;
import com.shibana.social_service.enums.profile_privacy_status.ProfileField;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
