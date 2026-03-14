package com.shibana.social_service.enums;

import com.shibana.social_service.entity.Profile;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.BiFunction;

@Slf4j
public enum ProfileField {
    BIO(
            (Profile targetProfile, String newValue) -> {
                String oldValue  = targetProfile.getBio();
                if (checkIsContentChanged(oldValue, newValue)) {
                    targetProfile.setBio(newValue);
                    return true;
                }
                return false;
            }),
    ADDRESS(
            (Profile targetProfile, String newValue) -> {
                String oldValue  = targetProfile.getAddress();
                if (checkIsContentChanged(oldValue, newValue)) {
                    targetProfile.setAddress(newValue);
                    return true;
                }
                return false;
            }),
    DOB(
            (Profile targetProfile, String newValue) -> {
                String oldValue = targetProfile.getDob() != null ? targetProfile.getDob().toString() : null;
                if (checkIsContentChanged(oldValue, newValue)) {
                    LocalDate newDob = newValue != null ? LocalDate.parse(newValue) : null;
                    targetProfile.setDob(newDob);
                    return true;
                }
                return false;
            }),
    PHONE(
            (Profile targetProfile, String newValue) -> {
                String oldValue  = targetProfile.getPhoneNumber();
                if (checkIsContentChanged(oldValue, newValue)) {
                    targetProfile.setPhoneNumber(newValue);
                    return true;
                }
                return false;
            });

    private final BiFunction<Profile, String, Boolean> updater;

    ProfileField(BiFunction<Profile, String, Boolean> updater) {
        this.updater = updater;
    }

    private static boolean checkIsContentChanged(String oldValue, String newValue) {
        return !Objects.equals(oldValue, newValue);
    }

    public boolean handleUpdate(Profile profile, String newValue) {
        return updater.apply(profile, newValue);
    }
}
