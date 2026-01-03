package com.shibana.identity_service.mapper;

import com.shibana.identity_service.dto.request.PermissionRequest;
import com.shibana.identity_service.dto.response.PermissionResponse;
import com.shibana.identity_service.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest permissionRequest);
    PermissionResponse toPermissionResponse(Permission permission);
}
