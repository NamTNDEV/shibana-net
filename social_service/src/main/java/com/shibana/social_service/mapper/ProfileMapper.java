package com.shibana.social_service.mapper;

import com.shibana.social_service.dto.ViewerContext;
import com.shibana.social_service.dto.request.ProfileCreationRequest;
import com.shibana.social_service.dto.response.*;
import com.shibana.social_service.entity.Profile;
import com.shibana.social_service.enums.profile_privacy_status.PrivacyLevel;
import com.shibana.social_service.enums.friendship_status.FriendshipStatus;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Mapper(componentModel = "spring")
public abstract class ProfileMapper {
    @Value("${service.media.static-url}")
    String mediaStaticUrl;

    // --- Common mappers ---
    public abstract Profile toProfileEntity(ProfileCreationRequest request);

    @Mapping(target = "displayName", source = "profile", qualifiedByName = "deriveDisplayName")
    public abstract AuthorProfileResponse toAuthorProfileResponse(Profile profile);

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
            @Context ViewerContext context
    );

    // --- After Mapping
    @AfterMapping
    protected void mapPrivacyToProfileFields(
            Profile profile,
            @MappingTarget ProfileDetailResponse response,
            @Context ViewerContext context
    ) {

        boolean isOwner = context.isOwner();
        boolean isFriended = (context.relationshipContext().friendshipStatus() == FriendshipStatus.FRIENDED);

        response.setViewerContext(context);
        response.setDob(wrapField(profile.getDob(), profile.getDobPrivacy(), isOwner, isFriended));
        response.setEmail(wrapField(profile.getEmail(), profile.getEmailPrivacy(), isOwner, isFriended));
        response.setAddress(wrapField(profile.getAddress(), profile.getAddressPrivacy(), isOwner, isFriended));
        response.setPhoneNumber(wrapField(profile.getPhoneNumber(), profile.getPhoneNumberPrivacy(), isOwner, isFriended));

        response.setBio(
                new ProfileFieldWithPrivacyResponse<>(profile.getBio(), PrivacyLevel.PUBLIC)
        );
    }

    // --- Helper Functions ---
    protected <T> ProfileFieldWithPrivacyResponse<T> wrapField(
            T fieldValue,
            PrivacyLevel privacyLevel,
            boolean isOwner,
            boolean isFriend
    ) {
        if (privacyLevel == null) {
            privacyLevel = PrivacyLevel.PUBLIC;
        }

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

    @Named("deriveDisplayName")
    protected String deriveDisplayName(Profile profile) {
        if (profile.getFirstName() != null && !profile.getFirstName().isBlank()
        && profile.getLastName() != null && !profile.getLastName().isBlank()) {
            return profile.getLastName() + " " + profile.getFirstName();
        } else {
            return "Anonymous";
        }
    }
}
