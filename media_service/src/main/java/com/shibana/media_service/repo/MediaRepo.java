package com.shibana.media_service.repo;

import com.shibana.media_service.entity.Media;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepo extends MongoRepository<Media, String> {
}
