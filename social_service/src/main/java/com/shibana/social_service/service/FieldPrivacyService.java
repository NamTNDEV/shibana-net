package com.shibana.social_service.service;

import com.shibana.social_service.dto.response.FieldPrivacyResponse;
import com.shibana.social_service.entity.FieldPrivacy;
import com.shibana.social_service.enums.PrivacyLevel;
import com.shibana.social_service.enums.ProfileField;
import com.shibana.social_service.exception.AppException;
import com.shibana.social_service.exception.ErrorCode;
import com.shibana.social_service.repo.jpa.FieldPrivacyRepo;
import com.shibana.social_service.repo.jpa.PrivacyRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FieldPrivacyService {
    PrivacyService privacyService;
    FieldPrivacyRepo  fieldPrivacyRepo;

    public List<FieldPrivacyResponse> getListByProfileId(String profileId) {
        return fieldPrivacyRepo.getListByProfileIdV2(profileId);
    }

    @Transactional("jpaTransactionManager")
    public void updateByProfileId(String profileId, PrivacyLevel privacyLevel, ProfileField fieldKey) {
        FieldPrivacy fp = fieldPrivacyRepo
                .getByProfileIdAndProfileField(profileId, fieldKey)
                .orElseThrow(() -> new AppException(ErrorCode.FIELD_PRIVACY_NOT_FOUND));

        if (fp.getPrivacy().getName() == privacyLevel) return;
        var updatePrivacy = privacyService.getPrivacyByLevel(privacyLevel);
        fp.setPrivacy(updatePrivacy);
        fieldPrivacyRepo.save(fp);
    }
}
