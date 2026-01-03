package com.shibana.identity_service.mapper;

import com.shibana.identity_service.dto.request.UserCreationRequest;
import com.shibana.identity_service.dto.request.UserUpdateRequest;
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
}
