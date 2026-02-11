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
    LocalDate dob;
    String address;
    String phoneNumber;
    String userId;
    String email;
    String username;
    String avatar;
    String bio;
    String cover;
}
