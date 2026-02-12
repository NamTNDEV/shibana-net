package com.shibana.identity_service.mapper;

import com.shibana.identity_service.dto.request.UserCreationRequest;
import com.shibana.identity_service.dto.request.UserUpdateRequest;
import com.shibana.identity_service.dto.response.MyAccountResponse;
import com.shibana.identity_service.dto.response.ProfileMetadataResponse;
import com.shibana.identity_service.dto.response.UserResponse;
import com.shibana.identity_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest userCreationRequest);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest userUpdateRequest);

    UserResponse toUserResponse(User user);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "roles", source = "user.roles")
    @Mapping(target = "firstName", source = "profileResponse.firstName")
    @Mapping(target = "lastName", source = "profileResponse.lastName")
    @Mapping(target = "avatar", source = "profileResponse.avatar")
    MyAccountResponse toGetMeResponse(User user, ProfileMetadataResponse profileResponse);
}
