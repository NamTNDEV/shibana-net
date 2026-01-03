package com.shibana.identity_service.mapper;

import com.shibana.identity_service.dto.request.ProfileCreationRequest;
import com.shibana.identity_service.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreationRequest toProfileCreationRequest(User user);
}
