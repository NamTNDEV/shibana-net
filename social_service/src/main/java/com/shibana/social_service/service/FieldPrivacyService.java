package com.shibana.social_service.service;

import com.shibana.social_service.dto.response.FieldPrivacyResponse;
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
    FieldPrivacyRepo  fieldPrivacyRepo;

    public List<FieldPrivacyResponse> getListByProfileId(String profileId) {
        return fieldPrivacyRepo.getListByProfileIdV2(profileId);
    }
}
