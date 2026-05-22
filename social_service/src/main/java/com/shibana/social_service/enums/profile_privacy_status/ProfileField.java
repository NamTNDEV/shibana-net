package com.shibana.social_service.enums.profile_privacy_status;

import com.shibana.social_service.entity.Profile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.function.TriFunction;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
public enum ProfileField {
    BIO(
            (Profile targetProfile, String newValue, PrivacyLevel newPrivacyLevel) -> {
                if (checkIsContentChanged(targetProfile.getBio(), newValue)) {
                    targetProfile.setBio(newValue);
                    return true;
                }
                return false;
            }),
    ADDRESS(
            (Profile targetProfile, String newValue, PrivacyLevel newPrivacyLevel) -> {
                boolean isChanged = false;
                if (checkIsContentChanged(targetProfile.getAddress(), newValue)) {
                    targetProfile.setAddress(newValue);
                    isChanged = true;
                }

                if (newPrivacyLevel != null && targetProfile.getAddressPrivacy() != newPrivacyLevel) {
                    targetProfile.setAddressPrivacy(newPrivacyLevel);
                    isChanged = true;
                }

                return isChanged;
            }),
    DOB(
            (Profile targetProfile, String newValue, PrivacyLevel newPrivacyLevel) -> {
                String oldValue = targetProfile.getDob() != null ? targetProfile.getDob().toString() : null;
                boolean isChanged = false;
                if (checkIsContentChanged(oldValue, newValue)) {
                    LocalDate newDob = newValue != null ? LocalDate.parse(newValue) : null;
                    targetProfile.setDob(newDob);
                    isChanged = true;
                }

                if (newPrivacyLevel != null && targetProfile.getDobPrivacy() != newPrivacyLevel) {
                    targetProfile.setDobPrivacy(newPrivacyLevel);
                    isChanged = true;
                }

                return isChanged;
            }),
    PHONE(
            (Profile targetProfile, String newValue, PrivacyLevel newPrivacyLevel) -> {
                boolean isChanged = false;
                if (checkIsContentChanged(targetProfile.getPhoneNumber(), newValue)) {
                    targetProfile.setPhoneNumber(newValue);
                    isChanged = true;
                }

                if (newPrivacyLevel != null && targetProfile.getPhoneNumberPrivacy() != newPrivacyLevel) {
                    targetProfile.setPhoneNumberPrivacy(newPrivacyLevel);
                    isChanged = true;
                }

                return isChanged;
            }),
    EMAIL(
            (Profile targetProfile, String newValue, PrivacyLevel newPrivacyLevel) -> {
                boolean isChanged = false;
                if (checkIsContentChanged(targetProfile.getEmail(), newValue)) {
                    targetProfile.setEmail(newValue);
                    isChanged = true;
                }

                if (newPrivacyLevel != null && targetProfile.getEmailPrivacy() != newPrivacyLevel) {
                    targetProfile.setEmailPrivacy(newPrivacyLevel);
                    isChanged = true;
                }

                return isChanged;
            });

    private final TriFunction<Profile, String, PrivacyLevel, Boolean> updater;

    ProfileField(TriFunction<Profile, String, PrivacyLevel, Boolean> updater) {
        this.updater = updater;
    }

    private static boolean checkIsContentChanged(String oldValue, String newValue) {
        return !Objects.equals(oldValue, newValue);
    }

    public boolean handleUpdate(Profile profile, String newValue, PrivacyLevel newPrivacyLevel) {
        return updater.apply(profile, newValue, newPrivacyLevel);
    }
}
