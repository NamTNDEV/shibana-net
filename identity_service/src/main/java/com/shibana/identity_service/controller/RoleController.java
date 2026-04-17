package com.shibana.identity_service.controller;

import com.shibana.identity_service.dto.request.RoleRequest;
import com.shibana.identity_service.dto.response.ApiResponse;
import com.shibana.identity_service.dto.response.RoleResponse;
import com.shibana.identity_service.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class RoleController {
    RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest roleRequest) {
        log.info("Create role endpoint called");
        return ApiResponse.<RoleResponse>builder()
                .code(201)
                .message("Role created successfully")
                .data(
                        roleService.create(roleRequest)
                )
                .build();
    }
}
