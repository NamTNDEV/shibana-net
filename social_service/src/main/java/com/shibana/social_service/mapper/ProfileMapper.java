package com.shibana.social_service.mapper;

import com.shibana.social_service.dto.request.ProfileCreationRequest;
import com.shibana.social_service.dto.request.ProfileUpdateRequest;
import com.shibana.social_service.dto.response.ProfileMetadataResponse;
import com.shibana.social_service.dto.response.ProfileResponse;
import com.shibana.social_service.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Profile toProfileEntity(ProfileCreationRequest request);
    ProfileResponse toProfileResponse(Profile entity);
    ProfileMetadataResponse toProfileMetadataResponse(Profile entity);
    void updateProfileFromRequest(@MappingTarget Profile entity, ProfileUpdateRequest request);
}
