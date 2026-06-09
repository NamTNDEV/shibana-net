package com.shibana.post_service.repo;

import com.shibana.post_service.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepo extends JpaRepository<Comment, UUID> {
    @Query(value = """
            SELECT *
            FROM comments
            WHERE post_id = :postId
            AND is_delete = false
            AND level = 0
            AND (:cursor IS NULL OR id < :cursor)
            ORDER BY id DESC
            LIMIT :size
            """, nativeQuery = true)
    List<Comment> getRootComments(
            @Param("postId") UUID postId,
            @Param("cursor") UUID cursor,
            @Param("size") int size
    );

    @Query(value = """
            SELECT *
            FROM comments
            WHERE parent_id = :parentId
            AND is_delete = false
            AND (:cursor IS NULL OR id > :cursor)
            ORDER BY id
            LIMIT :size
            """, nativeQuery = true)
    List<Comment> getReplyComments(
            @Param("parentId") UUID parentId,
            @Param("cursor") UUID cursor,
            @Param("size") int size
    );

    @Modifying
    @Query(value = """
            UPDATE comments
            SET reply_count = reply_count + :modifiedCount
            WHERE path @> cast(:childPath as ltree) AND id != :childId
            """,
            nativeQuery = true)
    void syncReplyCountForAncestors(@Param("childPath") String childPath, @Param("childId") UUID childId, @Param("modifiedCount") int modifiedCount);

    @Query(value = """
            SELECT COUNT(*) FROM comments 
            WHERE path <@ cast(:parentPath as ltree) 
            AND is_delete = false
            """, nativeQuery = true)
    int countActiveDescendants(@Param("parentPath") String parentPath);

    @Modifying
    @Query(value = """
            UPDATE comments 
            SET is_delete = true 
            WHERE path <@ cast(:parentPath as ltree)
            """, nativeQuery = true)
    void softDeleteSubTree(@Param("parentPath") String parentPath);
}
