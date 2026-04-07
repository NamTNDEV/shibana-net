package com.shibana.post_service.service;

import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.http_client.SocialClient;
import com.shibana.post_service.mapper.PostMapper;
import com.shibana.post_service.model.dto.response.PostResponse;
import com.shibana.post_service.model.dto.resquest.PostUpdateRequestBody;
import com.shibana.post_service.model.embedded.Author;
import com.shibana.post_service.model.entity.Post;
import com.shibana.post_service.model.enums.PostPrivacyEnum;
import com.shibana.post_service.model.service_command.posts.PostCreationCommand;
import com.shibana.post_service.repo.PostRepo;
import com.shibana.post_service.utils.HashtagUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostCommandService {
    PostRepo postRepo;
    PostMapper postMapper;
    SocialClient socialClient;
    MongoTemplate mongoTemplate;

    public PostResponse createPost(PostCreationCommand postCreationCommand) {
        String content = postCreationCommand.content();
        var privacy = postCreationCommand.privacy();

        Author author = socialClient.getAuthorProfileByUserId(postCreationCommand.authorId()).getData();
        Post post = Post.builder()
                .content(content)
                .author(author)
                .hashtags(HashtagUtils.extractFromContent(content))
                .privacy(privacy)
                .build();
        Post createdPost = postRepo.save(post);
        return postMapper.toPostResponse(createdPost);
    }

    public void updatePostById(String postId, String authorId, PostUpdateRequestBody body) {
        String content = body.getContent();
        PostPrivacyEnum privacy = body.getPrivacy();

        Post post = postRepo.getPostById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getAuthor().getUserId().equals(authorId)) {
            log.error("User {} attempted to update post {} which they do not own", authorId, postId);
            throw new AppException(ErrorCode.POST_UPDATE_DENIED);
        }

        if (!post.getContent().equals(body.getContent()) || post.getPrivacy() != body.getPrivacy()) {
            log.info("Updating post {} for user {}. New content: {}, New privacy: {}", postId, authorId, content, privacy);
            post.setContent(content);
            post.setPrivacy(privacy);
            postRepo.save(post);
        } else {
            log.info("The update content and privacy are the same as the existing ones. No update performed for post {}", postId);
        }
    }

    public void deleteById(String postId, String authorId) {
        Post post = postRepo.getPostById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getAuthor().getUserId().equals(authorId)) {
            log.error("User {} attempted to delete post {} which they do not own", authorId, postId);
            throw new AppException(ErrorCode.POST_DELETE_DENIED);
        }

        postRepo.deleteById(postId);
    }

    public void adjustCommentCount(String postId, int delta) {
        postRepo.adjustCommentCount(postId, delta);
    }

}
