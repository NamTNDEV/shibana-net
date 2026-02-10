package com.shibana.profile_service.entity;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
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

    @Property("user_id")
    String userId;

    @Property("first_name")
    String firstName;

    @Property("last_name")
    String lastName;

    String avatar;
    String cover;

    @Size(max = 160, message = "INVALID_BIO_LENGTH")
    String bio;

    LocalDate dob;
    String address;
    String phoneNumber;
}
