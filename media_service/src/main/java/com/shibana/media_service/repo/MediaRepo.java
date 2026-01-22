package com.shibana.media_service.repo;

import com.shibana.media_service.entity.Media;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaRepo extends MongoRepository<Media, String> {
    Optional<Media> findByFileName(String fileName);
}
