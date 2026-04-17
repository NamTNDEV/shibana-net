package com.shibana.identity_service.controller;

import com.shibana.identity_service.dto.request.PermissionRequest;
import com.shibana.identity_service.dto.response.ApiResponse;
import com.shibana.identity_service.dto.response.PermissionResponse;
import com.shibana.identity_service.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> addPermission(@RequestBody PermissionRequest permissionRequest) {
      return ApiResponse.<PermissionResponse>builder()
              .code(201)
              .message("Permission added successfully")
              .data(
                      permissionService.create(permissionRequest)
              )
              .build();
    };
}
