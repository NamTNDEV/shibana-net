package com.shibana.identity_service.dto.request;

import com.shibana.identity_service.validator.DobConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @Email(message = "INCORRECT_CREDENTIALS")
    @NotNull(message = "INCORRECT_CREDENTIALS")
    String email;

    @NotNull(message = "INCORRECT_CREDENTIALS")
    String password;

    String firstName;
    String lastName;

    @DobConstraint
    @NotNull
    LocalDate dob;
}
