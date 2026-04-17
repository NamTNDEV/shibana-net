package com.shibana.identity_service.controller;

import com.shibana.identity_service.dto.request.UserCreationRequest;
import com.shibana.identity_service.dto.request.UserUpdateRequest;
import com.shibana.identity_service.dto.response.ApiResponse;
import com.shibana.identity_service.dto.response.MyAccountResponse;
import com.shibana.identity_service.dto.response.UserResponse;
import com.shibana.identity_service.entity.User;
import com.shibana.identity_service.mapper.UserMapper;
import com.shibana.identity_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserController {
    UserService userService;

    @GetMapping("/me/account")
    ApiResponse<MyAccountResponse> getMyAccount(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getClaimAsString("user_id"));
        MyAccountResponse userResponse = userService.getMyAccount(userId);
        return ApiResponse.<MyAccountResponse>builder()
                .code(200)
                .data(userResponse)
                .message("User info fetched successfully.")
                .build();
    }
}
