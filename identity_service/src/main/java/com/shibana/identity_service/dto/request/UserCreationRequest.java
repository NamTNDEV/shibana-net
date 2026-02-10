package com.shibana.identity_service.dto.request;

import com.shibana.identity_service.entity.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3, max = 50, message = "INVALID_EMAIL")
    String email;

    @Size(min = 3, max = 50, message = "INVALID_USERNAME")
    String username;

    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;

    @Column(nullable = true)
    Set<Role> roles;

    String firstName;
    String lastName;
    LocalDate dob;
}
