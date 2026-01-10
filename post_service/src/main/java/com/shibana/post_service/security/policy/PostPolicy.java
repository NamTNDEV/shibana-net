package com.shibana.post_service.security.policy;

import com.shibana.post_service.entity.Post;
import com.shibana.post_service.enums.RoleEnum;
import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.repo.PostRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component("postPolicy")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostPolicy {
    PostRepo postRepo;

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities()
                .stream()
                .anyMatch(
                        a ->
                                a.getAuthority()
                                        .equals("ROLE_" + RoleEnum.ADMIN)
                );
    }

    private boolean isAuthor(String postId, String authorId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        return post.getAuthorId().equals(authorId);
    }

    public boolean canViewPost(String postId, Authentication auth) {
        if (isAdmin(auth)) {
            return true;
        }

        if (!(auth instanceof JwtAuthenticationToken jwtAuthToken)) {
            log.error("Authentication is not of type JwtAuthenticationToken");
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        Jwt jwt = jwtAuthToken.getToken();
        String authorId = jwt.getClaimAsString("user_id");

        if (!isAuthor(postId, authorId)) {
            log.error("User is not the author of the post");
            throw new AppException(ErrorCode.FORBIDDEN_OPERATION);
        }
        return true;
    }
}
