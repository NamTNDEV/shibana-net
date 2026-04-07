package com.shibana.social_service.dto.response;

import com.shibana.social_service.enums.profile_privacy_status.PrivacyLevel;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileFieldWithPrivacyResponse<T> {
    T value;
    PrivacyLevel privacyLevel;
}
