package com.shibana.identity_service.service;

import com.shibana.identity_service.dto.request.RoleRequest;
import com.shibana.identity_service.dto.response.RoleResponse;
import com.shibana.identity_service.entity.Permission;
import com.shibana.identity_service.entity.Role;
import com.shibana.identity_service.exception.AppException;
import com.shibana.identity_service.exception.ErrorCode;
import com.shibana.identity_service.mapper.RoleMapper;
import com.shibana.identity_service.repository.RoleRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepo roleRepo;
    PermissionService permissionService;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest roleRequest) {
        if(roleRepo.existsByName(roleRequest.getName())) {
            throw new AppException(ErrorCode.ROLE_ALREADY_EXISTS);
        }
        Role role = roleMapper.toRole(roleRequest);
        Set<UUID> inputedPermissionIds = roleRequest.getPermissionIds();
        if (inputedPermissionIds != null) {
            var listPermissionIds = permissionService.getPermissionsByIds(inputedPermissionIds);
            Set<Permission> permissions = new HashSet<>(listPermissionIds);
            role.setPermissions(permissions);
        }
        roleRepo.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public Role getRoleByName(String roleName) {
        return roleRepo.findByName(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
    }
}
