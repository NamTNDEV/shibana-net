package com.shibana.social_service.service;

import com.shibana.social_service.entity.FieldPrivacy;
import com.shibana.social_service.entity.Privacy;
import com.shibana.social_service.enums.PrivacyLevel;
import com.shibana.social_service.enums.ProfileField;
import com.shibana.social_service.repo.jpa.FieldPrivacyRepo;
import com.shibana.social_service.repo.jpa.PrivacyRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class PrivacyService {
    PrivacyRepo privacyRepo;
    FieldPrivacyRepo fieldPrivacyRepo;

    public List<Privacy> getPrivacyList() {
        return  privacyRepo.findAll();
    }

    @Transactional("jpaTransactionManager")
    public void initDefaultFieldsPrivacyForProfile(String profileId) {
        Privacy publicDefaultPrivacy = privacyRepo.findByName(PrivacyLevel.PUBLIC);
        List<FieldPrivacy> defaultProfileFieldPrivacyList = Arrays.stream(ProfileField.values())
                .map(field -> FieldPrivacy.builder()
                        .privacy(publicDefaultPrivacy)
                        .profileId(profileId)
                        .profileField(field)
                        .build()
                ).toList();
        fieldPrivacyRepo.saveAll(defaultProfileFieldPrivacyList);
    }
}
