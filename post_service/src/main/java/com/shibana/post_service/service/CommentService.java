package com.shibana.post_service.service;

import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.http_client.SocialClient;
import com.shibana.post_service.mapper.CommentMapper;
import com.shibana.post_service.model.dto.response.CommentResponse;
import com.shibana.post_service.model.dto.response.PageResponse;
import com.shibana.post_service.model.embedded.Author;
import com.shibana.post_service.model.entity.Comment;
import com.shibana.post_service.model.entity.Post;
import com.shibana.post_service.model.service_command.comments.CommentCreationCommand;
import com.shibana.post_service.repo.CommentRepo;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepo commentRepo;
    PostService postService;
    SocialClient socialClient;
    CommentMapper commentMapper;

    @Transactional
    public void createComment(CommentCreationCommand command) {
        log.info(":: Create comment controller ::");

        if (!postService.checkPostIsExist(command.postId())) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }

        Comment parentComment = null;
        if (command.parentId() != null && !command.parentId().isBlank()) {
            parentComment = commentRepo.findById(command.parentId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        }

        Author author = socialClient.getAuthorProfileByUserId(command.commnentorId()).getData();

        int commentLevel = parentComment == null ? 0 : parentComment.getLevel() + 1;
        String commentPath = generateCommentPath(parentComment);

        Comment comment = Comment.builder()
                .author(author)
                .content(command.content())
                .postId(command.postId())
                .level(commentLevel)
                .path(commentPath)
                .build();

        commentRepo.save(comment);

        if (parentComment != null && parentComment.getId() != null) {
            commentRepo.incReplyCount(parentComment.getId());
        }
    }

    public PageResponse<CommentResponse> getRootCommentsByPostId(String postId, int page, int size) {
        if (postId == null || postId.isBlank()) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }

        Post post = postService.getPostById(postId);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<Comment> commentSlice = commentRepo.findRootCommentsByPostId(post.getId(), pageRequest);
        List<CommentResponse> commentResponses = commentSlice.getContent().stream()
                .map(commentMapper::toCommentResponse)
                .toList();

        return PageResponse.<CommentResponse>builder()
                .page(page)
                .size(size)
                .hasNext(commentSlice.hasNext())
                .payload(commentResponses)
                .build();
    }

    String generateCommentPath(Comment parentComment) {
        if (parentComment == null) {
            return null;
        } else if (parentComment.getPath() == null) {
            return "," + parentComment.getId() + ",";
        }

        return parentComment.getPath() + parentComment.getId() + ",";
    }
}
