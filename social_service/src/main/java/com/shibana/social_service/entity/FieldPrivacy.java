package com.shibana.social_service.entity;

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
                @UniqueConstraint(columnNames = {"profile_id", "profile_field"})
        },
        indexes = {
                @Index(name = "idx_field_privacy_profile_id", columnList = "profile_id")
        }
)
public class FieldPrivacy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "profile_id", nullable = false)
    String profileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_field",  nullable = false)
    ProfileField  profileField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="privacy_id", nullable = false)
    Privacy privacy;
}
