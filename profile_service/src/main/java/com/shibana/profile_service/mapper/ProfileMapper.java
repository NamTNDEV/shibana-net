package com.shibana.profile_service.mapper;

import com.shibana.profile_service.dto.request.ProfileCreationRequest;
import com.shibana.profile_service.dto.request.ProfileUpdateRequest;
import com.shibana.profile_service.dto.response.ProfileResponse;
import com.shibana.profile_service.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Profile toProfileEntity(ProfileCreationRequest request);
    ProfileResponse toProfileResponse(Profile entity);
    void updateProfileFromRequest(@MappingTarget Profile entity, ProfileUpdateRequest request);
}
