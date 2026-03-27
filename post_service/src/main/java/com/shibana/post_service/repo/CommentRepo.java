package com.shibana.post_service.repo;

import com.shibana.post_service.model.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepo extends MongoRepository<Comment, String> {
    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'replyCount': 1 } }")
    void incReplyCount(String commentId);

    @Query("{ 'postId': ?0, 'path': null }")
    Slice<Comment> findRootCommentsByPostId(String postId, Pageable  pageable);
}
