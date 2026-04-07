package com.shibana.social_service.service;

import com.shibana.social_service.dto.response.FieldPrivacyResponse;
import com.shibana.social_service.entity.FieldPrivacy;
import com.shibana.social_service.enums.profile_privacy_status.PrivacyLevel;
import com.shibana.social_service.enums.profile_privacy_status.ProfileField;
import com.shibana.social_service.exception.AppException;
import com.shibana.social_service.exception.ErrorCode;
import com.shibana.social_service.repo.jpa.FieldPrivacyRepo;
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

    public List<FieldPrivacyResponse> getListByUserId(String userId) {
        return fieldPrivacyRepo.getListByUserIdV2(userId);
    }

    @Transactional("jpaTransactionManager")
    public void updateByUserId(String userId, PrivacyLevel privacyLevel, ProfileField fieldKey) {
        FieldPrivacy fp = fieldPrivacyRepo
                .getByUserIdAndProfileField(userId, fieldKey)
                .orElseThrow(() -> new AppException(ErrorCode.FIELD_PRIVACY_NOT_FOUND));

        if (fp.getPrivacy() == privacyLevel) return;
        fp.setPrivacy(privacyLevel);
        fieldPrivacyRepo.save(fp);
    }
}
