package com.shibana.social_service.entity;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Node("user_profiles")
public class Profile {
    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    String id;

    String userId;

    String username;

    String firstName;

    String lastName;

    String phoneNumber;

    @Size(max = 160, message = "INVALID_BIO_LENGTH")
    String bio;

    String avatarMediaId;
    String coverMediaId;
    Double coverPositionY;

    LocalDate dob;
    String address;
}
