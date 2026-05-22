package com.shibana.identity_service.service;

import com.shibana.identity_service.dto.request.UserCreationRequest;
import com.shibana.identity_service.dto.response.MyAccountResponse;
import com.shibana.identity_service.dto.response.ProfileMetadataResponse;
import com.shibana.identity_service.entity.Role;
import com.shibana.identity_service.entity.User;
import com.shibana.identity_service.exception.AppException;
import com.shibana.identity_service.exception.ErrorCode;
import com.shibana.identity_service.mapper.UserMapper;
import com.shibana.identity_service.message.dto.EventType;
import com.shibana.identity_service.message.dto.payload.UserRegisteredEventPayload;
import com.shibana.identity_service.message.outbox.dto.OutboxCreationRequest;
import com.shibana.identity_service.message.outbox.service.OutboxService;
import com.shibana.identity_service.message.outbox.enums.AggregateType;
import com.shibana.identity_service.repository.UserRepo;
import com.shibana.identity_service.repository.http_client.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.shibana.identity_service.enums.RoleEnum;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    RoleService roleService;
    OutboxService  outboxService;
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

        Instant now = Instant.now();
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
        user.setCreatedAt(now);

        try {
            user = userRepo.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Error creating user: {}", e.getMessage());
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        var eventPayload = new UserRegisteredEventPayload(
                user.getId(),
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getDob(),
                user.getUsername(),
                user.getEmail(),
                now
        );

        var requestPayload = OutboxCreationRequest.builder()
                .aggregateId(user.getId().toString())
                .aggregateType(AggregateType.USER.name())
                .eventType(EventType.USER_REGISTERED)
                .eventVersion(0)
                .eventPayload(eventPayload)
                .createdAt(now)
                .build();

        outboxService.creatAndPublishOutboxEvent(requestPayload);

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
