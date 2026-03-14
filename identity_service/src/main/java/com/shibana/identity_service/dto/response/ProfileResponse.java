package com.shibana.identity_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
    String firstName;
    String lastName;
    String userId;
    String username;
    String avatar;
    String cover;

    String bio;
    LocalDate dob;
    String address;
    String phoneNumber;
}
