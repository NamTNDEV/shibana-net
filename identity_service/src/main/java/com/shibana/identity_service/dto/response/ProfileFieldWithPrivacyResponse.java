package com.shibana.identity_service.dto.response;

import com.shibana.identity_service.enums.PrivacyLevel;
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
