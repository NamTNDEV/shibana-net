package com.shibana.profile_service.service;

import com.shibana.profile_service.dto.request.ProfileCreationRequest;
import com.shibana.profile_service.dto.response.ProfileResponse;
import com.shibana.profile_service.entity.Profile;
import com.shibana.profile_service.mapper.ProfileMapper;
import com.shibana.profile_service.repo.ProfileRepo;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProfileService {
    ProfileRepo profileRepo;
    ProfileMapper profileMapper;

    public ProfileResponse createProfile(ProfileCreationRequest request) {
        Profile profileRequest = profileMapper.toProfileEntity(request);
        Profile savedProfile = profileRepo.save(profileRequest);
        return profileMapper.toProfileResponse(savedProfile);
//        return ProfileResponse.builder()
//                .firstName(savedProfile.getFirstName())
//                .lastName(savedProfile.getLastName())
//                .phoneNumber(profileRequest.getPhoneNumber())
//                .address(savedProfile.getAddress())
//                .dob(savedProfile.getDob())
//                .build();
    }

    public ProfileResponse getProfile(String id) {
        Profile profile = profileRepo.findById(id).orElseThrow(
                () -> {
                    log.error("Profile with id {} not found", id);
                    return new RuntimeException("Profile not found");
                }
        );
        return profileMapper.toProfileResponse(profile);
    }

    public void deleteProfile(String id) {
        profileRepo.deleteById(id);
    }
}
