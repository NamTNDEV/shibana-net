package com.shibana.social_service.entity;

import com.shibana.social_service.enums.PrivacyLevel;
import com.shibana.social_service.enums.ProfileField;
import jakarta.persistence.*;
        import lombok.*;
        import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        name = "field_privacy",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "profile_field"})
        },
        indexes = {
                @Index(name = "idx_field_privacy_user_id", columnList = "user_id"),
        }
)
public class FieldPrivacy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id", nullable = false)
    String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_field",  nullable = false)
    ProfileField  profileField;

    @Enumerated(EnumType.STRING)
    @Column(name="privacy", nullable = false)
    PrivacyLevel privacy;
}
