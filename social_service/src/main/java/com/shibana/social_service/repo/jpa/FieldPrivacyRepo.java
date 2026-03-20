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
    @Query("SELECT fp FROM FieldPrivacy fp JOIN FETCH fp.privacy WHERE fp.userId = :userId")
    List<FieldPrivacy> getListByUserIdV1(@Param("userId") String userId);

    @Query("SELECT new com.shibana.social_service.dto.response.FieldPrivacyResponse(fp.profileField, fp.privacy) " +
            "FROM FieldPrivacy fp " +
            "WHERE fp.userId = :userId")
    List<FieldPrivacyResponse> getListByUserIdV2(@Param("userId") String userId);

    @Query("SELECT fp FROM FieldPrivacy fp WHERE fp.userId = :userId AND fp.profileField = :profileField")
    Optional<FieldPrivacy> getByUserIdAndProfileField(@Param("userId") String userId, @Param("profileField") ProfileField profileField);
}
