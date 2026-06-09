package com.shibana.post_service.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.mapper.CommentMapper;
import com.shibana.post_service.model.dto.response.CommentResponse;
import com.shibana.post_service.model.dto.response.PageResponse;
import com.shibana.post_service.model.entity.Comment;
import com.shibana.post_service.model.service_command.comments.CommentRootCreationCommand;
import com.shibana.post_service.model.service_command.comments.CommentUpdateCommand;
import com.shibana.post_service.model.service_command.comments.ReplyCommentCreationCommand;
import com.shibana.post_service.repo.CommentRepo;
import com.shibana.post_service.repo.PostRepo;
import com.shibana.post_service.utils.UuidUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepo commentRepo;
    AuthorService authorService;
    PostQueryService postQueryService;
    PostCommandService postCommandService;
    CommentMapper commentMapper;
    private final PostRepo postRepo;

    int MAX_LEVEL_COMMENT = 2;

    @Transactional
    public CommentResponse createRootComment(CommentRootCreationCommand command) {
        var targetPost = postQueryService.getPostById(command.postId());
        var author = authorService.findExistedAuthor(command.commnentorId());

        UUID commentId = UuidCreator.getTimeOrderedEpoch();
        Comment comment = Comment.builder()
                .id(commentId)
                .postId(targetPost.getId())
                .content(command.content())
                .path(generateCommentPath(null, commentId))
                .authorId(command.commnentorId())
                .build();
        var result = commentRepo.save(comment);

        return commentMapper.toCommentResponse(result, author);
    }

    @Transactional
    public CommentResponse createReplyComment(ReplyCommentCreationCommand command) {
        var parentComment = getCommentById(command.parentId());
        var author = authorService.findExistedAuthor(command.commnentorId());

        UUID newChildId = UuidCreator.getTimeOrderedEpoch();

        int targetLevel = parentComment.getLevel() + 1;
        UUID actualParentId = parentComment.getId();
        String actualParentPath = parentComment.getPath();

        if (targetLevel > MAX_LEVEL_COMMENT) {
            var grandParentComment = getCommentById(parentComment.getParentId());

            targetLevel = grandParentComment.getLevel() + 1;
            actualParentId = grandParentComment.getId();
            actualParentPath = grandParentComment.getPath();
        }

        String childPath = generateCommentPath(actualParentPath, newChildId);
        Comment comment = Comment.builder()
                .id(newChildId)
                .postId(parentComment.getPostId())
                .content(command.content())
                .path(childPath)
                .parentId(actualParentId)
                .authorId(command.commnentorId())
                .level(targetLevel)
                .build();

        var result = commentRepo.save(comment);

        commentRepo.syncReplyCountForAncestors(childPath, comment.getId(), 1);

        return commentMapper.toCommentResponse(result, author);
    }

    @Transactional
    public void deleteComment(String authorId, String commentId) {
//        Comment targetComment = safetyGetModifiedComment(commentId, authorId);

//        if (Boolean.TRUE.equals(targetComment.getIsDeleted())) {
//            throw new AppException(ErrorCode.COMMENT_DELETE_FAILED);
//        }
//
//        String basePath = targetComment.getPath() == null ? "," : targetComment.getPath();
//        String regexDescendants = "^" + basePath + targetComment.getId() + ",";
//
//        int deletedDescendantsCount = commentRepo.softDeleteDescendants(targetComment.getPostId(), regexDescendants);

//        targetComment.setIsDeleted(true);
//        commentRepo.save(targetComment);

//        int delta = deletedDescendantsCount + 1;
//        postCommandService.adjustCommentCount(targetComment.getPostId(), -delta);
//
//        if (targetComment.getPath() != null) {
//            List<String> ancestorIds = getAncestorIds(targetComment.getPath());
//            commentRepo.adjustReplyCountForAncestors(ancestorIds, -delta);
//        }
    }

    @Transactional
    public void updateComment(CommentUpdateCommand command) {
//        String commentId = command.commentId();
//        String updaterId = command.updaterId();
//        Comment existedComment = safetyGetModifiedComment(commentId, updaterId);

//        existedComment.setIsEdited(true);
//        existedComment.setContent(command.content());
//        commentRepo.save(existedComment);
    }

    public PageResponse<CommentResponse> getRootCommentsByPostId(String postId, int page, int size) {
//        if (postId == null || postId.isBlank()) {
//            throw new AppException(ErrorCode.POST_NOT_FOUND);
//        }

//        Post post = postQueryService.getPostById(postId);

//        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//        Slice<Comment> commentSlice = commentRepo.findRootCommentsByPostId(post.getId(), pageRequest);
//        List<CommentResponse> commentResponses = commentSlice.getContent().stream()
//                .map(commentMapper::toCommentResponse)
//                .toList();

//        return PageResponse.<CommentResponse>builder()
//                .page(page)
//                .size(size)
//                .hasNext(commentSlice.hasNext())
//                .payload(commentResponses)
//                .build();

        return null;
    }

    public PageResponse<CommentResponse> getRepliesByCommentId(String postId, String parentCommentId, int page, int size) {
//        if (postId == null || postId.isBlank()) {
//            throw new AppException(ErrorCode.POST_NOT_FOUND);
//        }
//        Post post = postQueryService.getPostById(postId);

//        if (parentCommentId == null || parentCommentId.isBlank()) {
//            throw new AppException(ErrorCode.COMMENT_NOT_FOUND);
//        }
//        var parent = getCommentById(parentCommentId);

//        String exactPath = generateCommentPath(parent);
//        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));

//        Slice<Comment> replies = commentRepo.findDirectReplies(post.getId(), exactPath, pageRequest);
//        List<CommentResponse> commentResponses = replies.getContent().stream()
//                .map(commentMapper::toCommentResponse)
//                .toList();

//        return PageResponse.<CommentResponse>builder()
//                .hasNext(replies.hasNext())
//                .page(page)
//                .size(size)
//                .payload(commentResponses)
//                .build();
        return null;
    }

    // --- Helpers ---
    private String generateCommentPath(String parentPath, UUID commentId) {
        String ltreeUUID = UuidUtils.formatUuidForLTree(commentId);
        return parentPath == null ? ltreeUUID : parentPath + "." + ltreeUUID;
    }

    Comment getCommentById(UUID commentId) {
        return commentRepo.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
