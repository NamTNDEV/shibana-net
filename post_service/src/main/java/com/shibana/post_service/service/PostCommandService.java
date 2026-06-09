package com.shibana.post_service.service;

import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.http_client.SocialClient;
import com.shibana.post_service.mapper.PostMapper;
import com.shibana.post_service.model.dto.response.PostResponse;
import com.shibana.post_service.model.dto.resquest.PostUpdateRequestBody;
import com.shibana.post_service.model.entity.Author;
import com.shibana.post_service.model.entity.Post;
import com.shibana.post_service.model.enums.PostPrivacyEnum;
import com.shibana.post_service.model.service_command.posts.PostCreationCommand;
import com.shibana.post_service.repo.AuthorRepo;
import com.shibana.post_service.repo.PostRepo;
import com.shibana.post_service.utils.HashtagUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostCommandService {
    PostRepo postRepo;

    PostMapper postMapper;

    AuthorService authorService;
    PostQueryService postQueryService;

    @Transactional
    public PostResponse createPost(PostCreationCommand postCreationCommand) {
        String content = postCreationCommand.content();
        var privacy = postCreationCommand.privacy();

        Author author = authorService.findExistedAuthor(postCreationCommand.authorId());

        Post post = Post.builder()
                .content(content)
                .authorId(author.getUserId())
                .hashtags(HashtagUtils.extractFromContent(content))
                .privacy(privacy)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Post createdPost = postRepo.save(post);
        return postMapper.toPostResponse(createdPost, author);
    }

    @Transactional
    public void updatePostById(UUID postId, PostUpdateRequestBody body) {
        Post post = postQueryService.getPostById(postId);

        if (!Objects.equals(post.getContent(), body.getContent())) {
            post.setContent(body.getContent());
        }

        if (!Objects.equals(post.getPrivacy(), body.getPrivacy())) {
            post.setPrivacy(body.getPrivacy());
        }
    }

    public void deleteById(String postId, String authorId) {
//        Post post = postQueryService.getPostById(postId);

//        if (!post.getAuthor().getUserId().equals(authorId)) {
//            log.error("User {} attempted to delete post {} which they do not own", authorId, postId);
//            throw new AppException(ErrorCode.POST_DELETE_DENIED);
//        }

//        postRepo.deleteById(postId);
    }

    public void adjustCommentCount(UUID postId, int delta) {
//        postRepo.adjustCommentCount(postId, delta);
    }

}
