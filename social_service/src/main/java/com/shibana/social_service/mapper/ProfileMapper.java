package com.shibana.social_service.mapper;

import com.shibana.social_service.dto.request.ProfileCreationRequest;
import com.shibana.social_service.dto.request.ProfileUpdateRequest;
import com.shibana.social_service.dto.response.ProfileMetadataResponse;
import com.shibana.social_service.dto.response.ProfileResponse;
import com.shibana.social_service.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = "spring")
public abstract class ProfileMapper {
    @Value("${service.media.static-url}")
    String mediaStaticUrl;

    public abstract Profile toProfileEntity(ProfileCreationRequest request);

    @Mapping(target = "avatar", source = "avatarMediaName", qualifiedByName = "toFullUrl")
    @Mapping(target = "cover", source = "coverMediaName", qualifiedByName = "toFullUrl")
    public abstract ProfileResponse toProfileResponse(Profile entity);

    @Mapping(target = "avatar", source = "avatarMediaName", qualifiedByName = "toFullUrl")
    public abstract ProfileMetadataResponse toProfileMetadataResponse(Profile entity);

    public abstract void updateProfileFromRequest(@MappingTarget Profile entity, ProfileUpdateRequest request);

    @Named("toFullUrl")
    protected String toFullUrl(String mediaName) {
        if (mediaName == null || mediaName.isEmpty()) {
            return null;
        }
        return mediaStaticUrl + mediaName;
    }
}
