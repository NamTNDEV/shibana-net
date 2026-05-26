package com.shibana.post_service.service;

import com.shibana.post_service.model.dto.resquest.AuthorCreationRequest;
import com.shibana.post_service.model.entity.Author;
import com.shibana.post_service.repo.AuthorRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorService {
    AuthorRepo authorRepo;

    @Transactional
    public void createAuthor(AuthorCreationRequest request) {
        Author author = Author.builder()
                .userId(request.getUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .createdAt(request.getCreatedAt())
                .build();

        authorRepo.save(author);
    }
}
