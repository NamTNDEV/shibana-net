package com.shibana.identity_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetMeResponse {
    String userId;
    String email;
    Set<RoleResponse> roles;
    String firstName;
    String lastName;
    LocalDate dob;
    String address;
    String phoneNumber;
    String avatar;
}
