package com.shibana.post_service.service;

import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.model.dto.resquest.AuthorCreationRequest;
import com.shibana.post_service.model.dto.resquest.AvatarUpdateRequest;
import com.shibana.post_service.model.entity.Author;
import com.shibana.post_service.repo.AuthorRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorService {
    AuthorRepo authorRepo;

    Author findExistedAuthor(UUID authorId) {
        return authorRepo.findById(authorId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.AUTHOR_NOT_FOUND)
                );
    }

    List<Author> findAllAuthors(Set<UUID> authorIds) {
        return authorRepo.findAllById(authorIds);
    }

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

    @Transactional
    public void updateAvatar(AvatarUpdateRequest request) {
        log.info("Updating avatar for userId: {}", request.getUserId());

        if (request.getAvatarMediaName() == null) {
            throw new AppException(ErrorCode.INVALID_AVATAR_MEDIA_NAME);
        }

        Author existedAuthor = findExistedAuthor(request.getUserId());
        existedAuthor.setAvatarMediaName(request.getAvatarMediaName());
        existedAuthor.setAvatarScale(request.getAvatarScale());
        existedAuthor.setAvatarPositionX(request.getAvatarPositionX());
        existedAuthor.setAvatarPositionY(request.getAvatarPositionY());

        authorRepo.save(existedAuthor);
        log.info("\uD83D\uDFE2 Updated avatar for userId: {}", request.getUserId());
    }
}
