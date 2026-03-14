package com.shibana.social_service.repo.jpa;

import com.shibana.social_service.dto.response.FieldPrivacyResponse;
import com.shibana.social_service.entity.FieldPrivacy;
import com.shibana.social_service.enums.ProfileField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FieldPrivacyRepo extends JpaRepository<FieldPrivacy, Long> {

    // Level 1
    @Query("SELECT fp FROM FieldPrivacy fp JOIN FETCH fp.privacy WHERE fp.profileId = :profileId")
    List<FieldPrivacy> getListByProfileIdV1(@Param("profileId") String profileId);

    @Query("SELECT new com.shibana.social_service.dto.response.FieldPrivacyResponse(fp.profileField, p.name) " +
            "FROM FieldPrivacy fp JOIN fp.privacy p " +
            "WHERE fp.profileId = :profileId")
    List<FieldPrivacyResponse> getListByProfileIdV2(@Param("profileId") String profileId);

    @Query("SELECT fp FROM FieldPrivacy fp WHERE fp.profileId = :profileId AND fp.profileField = :profileField")
    Optional<FieldPrivacy> getByProfileIdAndProfileField(@Param("profileId") String profileId, @Param("profileField") ProfileField profileField);
}
