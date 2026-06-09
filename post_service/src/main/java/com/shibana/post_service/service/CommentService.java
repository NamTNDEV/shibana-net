package com.shibana.post_service.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.exception.ErrorCode;
import com.shibana.post_service.mapper.CommentMapper;
import com.shibana.post_service.model.dto.response.CommentResponse;
import com.shibana.post_service.model.dto.response.CursorResponse;
import com.shibana.post_service.model.entity.Author;
import com.shibana.post_service.model.entity.Comment;
import com.shibana.post_service.model.service_command.comments.CommentRootCreationCommand;
import com.shibana.post_service.model.service_command.comments.CommentUpdateCommand;
import com.shibana.post_service.model.service_command.comments.ReplyCommentCreationCommand;
import com.shibana.post_service.repo.CommentRepo;
import com.shibana.post_service.utils.UuidUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    Comment getCommentById(UUID commentId) {
        return commentRepo.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    }

    /**
     * Deffered Solution: Hàm 'postCommandService' đang không ổn khi high-concurrency,
     * triển khai sau (gợi ý: Redis-based, Kafka-based,...)
     */
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
        postCommandService.adjustCommentCount(targetPost.getId(), 1);

        return commentMapper.toCommentResponse(result, author);
    }

    /**
     * Deffered Solution: Hàm 'postCommandService' đang không ổn khi high-concurrency,
     * triển khai sau (gợi ý: Redis-based, Kafka-based,...)
     */
    @Transactional
    public CommentResponse createReplyComment(ReplyCommentCreationCommand command) {
        var parentComment = getCommentById(command.parentId());
        var author = authorService.findExistedAuthor(command.commnentorId());

        UUID newChildId = UuidCreator.getTimeOrderedEpoch();

        int targetLevel = parentComment.getLevel() + 1;
        UUID actualParentId = parentComment.getId();
        String actualParentPath = parentComment.getPath();

        int MAX_LEVEL_COMMENT = 2;
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
        postCommandService.adjustCommentCount(parentComment.getPostId(), 1);

        return commentMapper.toCommentResponse(result, author);
    }

    public CursorResponse<CommentResponse> getRootCommentsByPostId(UUID postUuid, String cursor, int size) {
        var post = postQueryService.getPostById(postUuid);

        int plusOneSize = size + 1;
        UUID actualCursor = cursor == null ? null : UuidCreator.getTimeOrderedEpoch();
        List<Comment> rootCommentLists = new ArrayList<>(commentRepo.getRootComments(post.getId(), actualCursor, plusOneSize));

        var hasNext = rootCommentLists.size() > size;
        if (hasNext) {
            rootCommentLists.removeLast();
        }
        String nextCursor = rootCommentLists.isEmpty() ? null : rootCommentLists.getLast().getId().toString();

        List<CommentResponse> commentResponses = mappingOwnerComments(rootCommentLists);
        return CursorResponse.<CommentResponse>builder()
                .payload(commentResponses)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .size(commentResponses.size())
                .build();
    }

    public CursorResponse<CommentResponse> getRepliesByCommentId(UUID parentId, String cursor, int size) {
        Comment parentComment = getCommentById(parentId);

        int plusOneSize = size + 1;
        UUID actualCursor = cursor == null ? null : UUID.fromString(cursor);
        List<Comment> replyCommentLists = new ArrayList<>(commentRepo.getReplyComments(parentComment.getId(), actualCursor, plusOneSize));
        var hasNext = replyCommentLists.size() > size;
        if (hasNext) {
            replyCommentLists.removeLast();
        }
        String nextCursor = replyCommentLists.isEmpty() ? null : replyCommentLists.getLast().getId().toString();

        List<CommentResponse> commentResponses = mappingOwnerComments(replyCommentLists);
        return CursorResponse.<CommentResponse>builder()
                .payload(commentResponses)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .size(commentResponses.size())
                .build();
    }

    @Transactional
    public void softDeleteComment(UUID authorId, UUID commentId) {
        Comment targetComment = getCommentById(commentId);

        if (!Objects.equals(targetComment.getAuthorId(), authorId)) {
            throw new AppException(ErrorCode.COMMENT_DELETE_DENIED);
        }

        String targetPath = targetComment.getPath();
        int deletedCount = commentRepo.countActiveDescendants(targetPath);
        if (deletedCount == 0) return;

        commentRepo.softDeleteSubTree(targetPath);

        commentRepo.syncReplyCountForAncestors(targetPath, targetComment.getId(), -deletedCount);
        postCommandService.adjustCommentCount(targetComment.getPostId(), -deletedCount);
    }

    /**
     * Deffered Solution: Cần bổ sung bảng lưu trữ
     * lịch sử thay đổi
     */
    @Transactional
    public CommentResponse updateComment(CommentUpdateCommand command) {
        var existingComment = getCommentById(command.commentId());

        if (!Objects.equals(existingComment.getAuthorId(), command.updaterId())) {
            throw new AppException(ErrorCode.COMMENT_UPDATE_DENIED);
        }

        existingComment.setContent(command.content());
        existingComment.setIsEdited(true);
        var result = commentRepo.save(existingComment);

        Author commentor = authorService.findExistedAuthor(existingComment.getAuthorId());
        return commentMapper.toCommentResponse(result, commentor);
    }

    // --- Helpers ---
    private String generateCommentPath(String parentPath, UUID commentId) {
        String ltreeUUID = UuidUtils.formatUuidForLTree(commentId);
        return parentPath == null ? ltreeUUID : parentPath + "." + ltreeUUID;
    }

    private List<CommentResponse> mappingOwnerComments(List<Comment> comments) {
        if (comments.isEmpty()) {
            return Collections.emptyList();
        }

        var ownerIds = comments.stream().map(Comment::getAuthorId).collect(Collectors.toSet());
        Map<UUID, Author> authorMap = authorService.findAllAuthors(ownerIds).stream()
                .collect(Collectors.toMap(Author::getUserId, author -> author));

        return comments.stream()
                .map(
                        comment -> commentMapper.toCommentResponse(
                                comment,
                                authorMap.getOrDefault(comment.getAuthorId(), authorService.getFallbackAuthor(comment.getAuthorId()))
                        )
                ).collect(Collectors.toList());
    }
}
