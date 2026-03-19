package com.shibana.social_service.service;

import com.shibana.social_service.entity.FieldPrivacy;
import com.shibana.social_service.enums.PrivacyLevel;
import com.shibana.social_service.enums.ProfileField;
import com.shibana.social_service.repo.jpa.FieldPrivacyRepo;
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
    FieldPrivacyRepo fieldPrivacyRepo;

    public List<PrivacyLevel> getPrivacyList() {
        return  Arrays.stream(PrivacyLevel.values()).toList();
    }

    @Transactional("jpaTransactionManager")
    public void initDefaultFieldsPrivacyForProfile(String profileId) {
        List<FieldPrivacy> defaultProfileFieldPrivacyList = Arrays.stream(ProfileField.values())
                .map(field -> {
                            var defaultPrivacy = field == ProfileField.EMAIL ? PrivacyLevel.PRIVATE : PrivacyLevel.PUBLIC;
                            return FieldPrivacy.builder()
                                    .privacy(defaultPrivacy)
                                    .profileId(profileId)
                                    .profileField(field)
                                    .build();
                        }
                ).toList();
        fieldPrivacyRepo.saveAll(defaultProfileFieldPrivacyList);
    }
}
