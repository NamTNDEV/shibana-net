package com.shibana.identity_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileCreationRequest {
    UUID userId;
    String username;
    String email;
    String firstName;
    String lastName;
    LocalDate dob;
}
