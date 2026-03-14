package com.shibana.social_service.repo.jpa;

import com.shibana.social_service.entity.Privacy;
import com.shibana.social_service.enums.PrivacyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivacyRepo extends JpaRepository<Privacy, Integer> {
    Optional<Privacy> findByName(PrivacyLevel name);
}
