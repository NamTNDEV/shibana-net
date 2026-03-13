package com.shibana.social_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
    String username;
    String firstName;
    String lastName;
    String userId;
    String avatar;
    String cover;
    Double coverPositionY;
    Double avatarScale;
    Double avatarPositionX;
    Double avatarPositionY;
    String bio;
    LocalDate dob;
    String address;
    String phoneNumber;
}
