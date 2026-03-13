package com.shibana.social_service.dto.response;

import com.shibana.social_service.enums.PrivacyLevel;
import com.shibana.social_service.enums.ProfileField;
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
