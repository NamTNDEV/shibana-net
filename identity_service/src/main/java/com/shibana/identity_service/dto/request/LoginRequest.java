package com.shibana.identity_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @Email(message = "INCORRECT_CREDENTIALS")
    @NotBlank(message = "INCORRECT_CREDENTIALS")
    private String email;

    @NotBlank(message = "INCORRECT_CREDENTIALS")
    private String password;
}
