package com.shibana.post_service.model.dto.resquest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AuthorCreationRequest {
    UUID userId;
    String firstName;
    String lastName;
    String username;
    Instant createdAt;
}
