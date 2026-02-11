package com.shibana.identity_service.repository;

import com.shibana.identity_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, String> {
    public boolean existsByEmail(String email);
    public boolean existsByUsername(String username);
    public Optional<User> findByEmail(String email);
}
