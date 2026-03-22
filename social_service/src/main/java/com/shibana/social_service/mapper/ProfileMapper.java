package com.shibana.social_service.mapper;

import com.shibana.social_service.dto.PrivacyContext;
import com.shibana.social_service.dto.ViewerContext;
import com.shibana.social_service.dto.request.ProfileCreationRequest;
import com.shibana.social_service.dto.request.ProfileUpdateRequest;
import com.shibana.social_service.dto.response.*;
import com.shibana.social_service.entity.Profile;
import com.shibana.social_service.enums.PrivacyLevel;
import com.shibana.social_service.enums.ProfileField;
import com.shibana.social_service.enums.friendship_status.FriendshipStatus;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Mapper(componentModel = "spring")
public abstract class ProfileMapper {
    @Value("${service.media.static-url}")
    String mediaStaticUrl;

    // --- Common mappers ---
    public abstract Profile toProfileEntity(ProfileCreationRequest request);

//    public abstract void updateProfileFromRequest(@MappingTarget Profile entity, ProfileUpdateRequest request);

    // --- Complex Mappers ---
    @Mapping(target = "avatar", source = "avatarMediaName", qualifiedByName = "toFullUrl")
    @Mapping(target = "cover", source = "coverMediaName", qualifiedByName = "toFullUrl")
    public abstract ProfileResponse toProfileResponse(Profile entity);

    @Mapping(target = "avatar", source = "avatarMediaName", qualifiedByName = "toFullUrl")
    public abstract ProfileMetadataResponse toProfileMetadataResponse(Profile entity);

    @Mapping(target = "avatar", source = "avatarMediaName", qualifiedByName = "toFullUrl")
    @Mapping(target = "cover", source = "coverMediaName", qualifiedByName = "toFullUrl")
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "dob", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "email",  ignore = true)
    @Mapping(target = "viewerContext", ignore = true)
    public abstract ProfileDetailResponse toProfileDetailResponse(
            Profile profile,
            @Context List<FieldPrivacyResponse> fieldPrivacyList,
            @Context ViewerContext context
    );

    // --- After Mapping
    @AfterMapping
    protected void mapPrivacyToProfileFields(
            Profile profile,
            @MappingTarget ProfileDetailResponse response,
            @Context List<FieldPrivacyResponse> fieldPrivacyList,
            @Context ViewerContext context
    ) {
        Map<ProfileField, PrivacyLevel> fieldPrivacyMap = fieldPrivacyList
                .stream()
                .collect(
                        Collectors.toMap(
                                FieldPrivacyResponse::getFieldKey,
                                FieldPrivacyResponse::getPrivacyLevel,
                                (oldValue, newValue) -> oldValue
                        )
                );
        boolean isOwner = context.isOwner();
        boolean isFriended = (context.relationshipContext().friendshipStatus() == FriendshipStatus.FRIENDED);

        response.setViewerContext(context);
        response.setBio(wrapField(profile.getBio(), ProfileField.BIO, fieldPrivacyMap, isOwner, isFriended));
        response.setDob(wrapField(profile.getDob(), ProfileField.DOB, fieldPrivacyMap, isOwner, isFriended));
        response.setEmail(wrapField(profile.getEmail(), ProfileField.EMAIL, fieldPrivacyMap, isOwner, isFriended));
        response.setAddress(wrapField(profile.getAddress(), ProfileField.ADDRESS, fieldPrivacyMap, isOwner, isFriended));
        response.setPhoneNumber(wrapField(profile.getPhoneNumber(), ProfileField.PHONE, fieldPrivacyMap, isOwner, isFriended));
    }

    // --- Helper Functions ---
    protected <T> ProfileFieldWithPrivacyResponse<T> wrapField(
            T fieldValue,
            ProfileField pf,
            Map<ProfileField, PrivacyLevel> fieldPrivacyMap,
            boolean isOwner,
            boolean isFriend
    ) {
        PrivacyLevel privacyLevel = fieldPrivacyMap.getOrDefault(pf, PrivacyLevel.PUBLIC);
        T maskedValue = fieldValue;
        if (!isOwner) {
            if (privacyLevel == PrivacyLevel.PRIVATE) {
                maskedValue = null;
            } else if (privacyLevel == PrivacyLevel.FRIENDS && !isFriend) {
                maskedValue = null;
            }
        }
        return new ProfileFieldWithPrivacyResponse<>(maskedValue, privacyLevel);
    }

    @Named("toFullUrl")
    protected String toFullUrl(String mediaName) {
        if (mediaName == null || mediaName.isEmpty()) {
            return null;
        }
        return mediaStaticUrl + mediaName;
    }
}
