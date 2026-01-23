package com.shibana.profile_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
    String firstName;
    String lastName;
    LocalDate dob;
    String address;
    String phoneNumber;
    String userId;
    String email;
}
