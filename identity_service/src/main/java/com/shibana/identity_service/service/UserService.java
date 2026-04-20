package com.shibana.identity_service.service;

import com.shibana.identity_service.dto.request.ProfileCreationRequest;
import com.shibana.identity_service.dto.request.UserCreationRequest;
import com.shibana.identity_service.dto.response.MyAccountResponse;
import com.shibana.identity_service.dto.response.ProfileMetadataResponse;
import com.shibana.identity_service.entity.Role;
import com.shibana.identity_service.entity.User;
import com.shibana.identity_service.exception.AppException;
import com.shibana.identity_service.exception.ErrorCode;
import com.shibana.identity_service.mapper.UserMapper;
import com.shibana.identity_service.repository.UserRepo;
import com.shibana.identity_service.repository.http_client.ProfileClient;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.shibana.identity_service.enums.RoleEnum;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    RoleService roleService;
    UserRepo userRepo;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    ProfileClient profileClient;

    public User getUserById(UUID id) {
        return userRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public User createUser(UserCreationRequest userRequest) {
        if (userRepo.existsByEmail(userRequest.getEmail()) || userRepo.existsByUsername(userRequest.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.toUser(userRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roles = new HashSet<>();
        Set<Role> creationRoles = userRequest.getRoles();
        if (creationRoles != null && !creationRoles.isEmpty()) {
            roles.addAll(creationRoles);
        } else {
            roles.add(roleService.getRoleByName(RoleEnum.USER.name()));
        }
        user.setRoles(roles);

        try {
            user = userRepo.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Error creating user: {}", e.getMessage());
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        ProfileCreationRequest profileCreationRequest = ProfileCreationRequest.builder()
                .userId(user.getId().toString())
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .dob(userRequest.getDob())
                .build();

        profileClient.create(profileCreationRequest);
        return user;
    }

    public MyAccountResponse getMyAccount(UUID userId) {
        User user = getUserById(userId);
        ProfileMetadataResponse metadataResponse = profileClient.getMetadataByUserId(userId).getData();
        return userMapper.toGetMeResponse(
                user,
                metadataResponse
        );
    }

    public boolean isUserExist(String email) {
        return userRepo.existsByEmail(email);
    }
}
