package com.shibana.social_service.repo.jpa;

import com.shibana.social_service.entity.FieldPrivacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldPrivacyRepo extends JpaRepository<FieldPrivacy, Long> {

}
