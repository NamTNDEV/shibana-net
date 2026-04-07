package com.shibana.social_service.dto.response;

import com.shibana.social_service.enums.profile_privacy_status.PrivacyLevel;
import com.shibana.social_service.enums.profile_privacy_status.ProfileField;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FieldPrivacyResponse {
    ProfileField fieldKey;
    PrivacyLevel privacyLevel;
}
