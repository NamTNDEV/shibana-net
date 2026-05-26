package com.shibana.social_service.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.shibana.social_service.enums.profile_privacy_status.PrivacyLevel;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Node("profiles")
public class Profile {
    @Id
    UUID userId;

    String username;

    String email;

    String firstName;

    String lastName;

    String phoneNumber;

    @Size(max = 160, message = "INVALID_BIO_LENGTH")
    String bio;

    String coverMediaName;
    Double coverPositionY;

    String avatarMediaName;
    Double avatarScale;
    Double avatarPositionX;
    Double avatarPositionY;

    LocalDate dob;
    String address;

    @Builder.Default
    PrivacyLevel emailPrivacy = PrivacyLevel.PRIVATE;

    @Builder.Default
    PrivacyLevel phoneNumberPrivacy = PrivacyLevel.PUBLIC;

    @Builder.Default
    PrivacyLevel dobPrivacy = PrivacyLevel.PUBLIC;

    @Builder.Default
    PrivacyLevel addressPrivacy = PrivacyLevel.PUBLIC;

    Instant createdAt;
    Instant updatedAt;

    @Version
    Long version;
}
