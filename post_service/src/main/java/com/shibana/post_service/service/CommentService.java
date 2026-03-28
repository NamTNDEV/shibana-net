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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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

        Comment parentComment = getParentComment(command.parentId());

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
        syncCounts(comment);
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

    public PageResponse<CommentResponse> getRepliesByCommentId(String postId, String parentCommentId, int page, int size) {
        if (postId == null || postId.isBlank()) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }
        Post post = postService.getPostById(postId);

        if (parentCommentId == null || parentCommentId.isBlank()) {
            throw new AppException(ErrorCode.COMMENT_NOT_FOUND);
        }
        var parent = getParentComment(parentCommentId);

        String exactPath = generateCommentPath(parent);
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Slice<Comment> replies = commentRepo.findDirectReplies(post.getId(), exactPath, pageRequest);
        List<CommentResponse> commentResponses = replies.getContent().stream()
                .map(commentMapper::toCommentResponse)
                .toList();

        return PageResponse.<CommentResponse>builder()
                .hasNext(replies.hasNext())
                .page(page)
                .size(size)
                .payload(commentResponses)
                .build();
    }

    // --- Helpers ---
    String generateCommentPath(Comment parentComment) {
        if (parentComment == null) {
            return null;
        } else if (parentComment.getPath() == null) {
            return "," + parentComment.getId() + ",";
        }

        return parentComment.getPath() + parentComment.getId() + ",";
    }

    Comment getParentComment(String parentId) {
        Comment parentComment = null;
        if (parentId != null && !parentId.isBlank()) {
            parentComment = commentRepo.findById(parentId)
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        }
        return parentComment;
    }

    void syncCounts(Comment comment) {
        postService.increaseCommentCount(comment.getPostId());

        if (comment.getPath() == null) return;

        String clearPath = comment.getPath().substring(1, comment.getPath().length() - 1);
        List<String> ancestorIds = Arrays.asList(clearPath.split(","));
        commentRepo.incrementReplyCountForAncestors(ancestorIds);
    }
}
