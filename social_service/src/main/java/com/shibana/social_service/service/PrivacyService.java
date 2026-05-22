package com.shibana.social_service.service;

import com.shibana.social_service.enums.profile_privacy_status.PrivacyLevel;
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
    public List<PrivacyLevel> getPrivacyList() {
        return  Arrays.stream(PrivacyLevel.values()).toList();
    }
}
