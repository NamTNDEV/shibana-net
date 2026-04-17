package com.shibana.identity_service.config;

import com.shibana.identity_service.dto.request.UserCreationRequest;
import com.shibana.identity_service.entity.Role;
import com.shibana.identity_service.enums.RoleEnum;
import com.shibana.identity_service.repository.PermissionRepo;
import com.shibana.identity_service.repository.RoleRepo;
import com.shibana.identity_service.repository.UserRepo;
import com.shibana.identity_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AppConfig {
    static String ADMIN_EMAIL = "shibana_admin@yopmail.com";
    static String ADMIN_PASSWORD = "123123";
    static String ADMIN_USERNAME = "shibana_admin";

    UserService userService;

    @Bean
    CommandLineRunner redisPing(StringRedisTemplate srt) {
        return args -> {
            try {
                srt.execute((RedisCallback<Object>) RedisConnectionCommands::ping);
            } catch (Exception e) {
                System.err.println("❌ Redis connect failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        };
    }

    @Bean
    public ApplicationRunner applicationRunner(UserRepo userRepo, RoleRepo roleRepo, PermissionRepo permissionRepo) {
        return args -> {
            if (userRepo.findByEmail(ADMIN_EMAIL).isPresent()) {
                log.warn("User with name {} already exists", ADMIN_EMAIL);
                return;
            }
            var allPermissions = permissionRepo.findAll();
            var adminRole = roleRepo.findByName(RoleEnum.ADMIN.name()).map(
                    r -> {
                        if (r.getPermissions() == null) {
                            r.setPermissions(new HashSet<>(allPermissions));
                            return roleRepo.save(r);
                        }
                        return r;
                    }).orElseGet(
                    () -> roleRepo.save(
                            Role.builder()
                                    .name(RoleEnum.ADMIN.name())
                                    .description("Administrator role with all permissions")
                                    .permissions(new HashSet<>(allPermissions))
                                    .build()
                    )
            );

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            userService.createUser(
                    UserCreationRequest.builder()
                            .email(ADMIN_EMAIL)
                            .username(ADMIN_USERNAME)
                            .password(ADMIN_PASSWORD)
                            .firstName("Super Admin")
                            .lastName("Shibana")
                            .roles(roles)
                            .dob(LocalDate.of(1990, 1, 1))
                            .build()
            );
        };
    }
}
