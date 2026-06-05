package com.shibana.post_service.security;

import com.shibana.post_service.model.entity.Post;
import com.shibana.post_service.repo.PostRepo;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("postSecurity")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class PostSecurityService {
    PostRepo postRepo;

    public boolean isOwner(Authentication authentication, String postId) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        UUID requesterId = UUID.fromString(jwt.getClaim("user_id"));
        UUID postUUID = UUID.fromString(postId);

        return postRepo.findById(postUUID)
                .map(Post::getAuthorId)
                .map(authorId -> authorId.equals(requesterId))
                .orElse(false);
    }
}
