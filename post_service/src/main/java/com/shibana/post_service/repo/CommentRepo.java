package com.shibana.post_service.repo;

import com.shibana.post_service.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepo extends JpaRepository<Comment, UUID> {
    @Modifying
    @Query(value = """
            UPDATE comments
            SET reply_count = reply_count + :modifiedCount
            WHERE path @> cast(:childPath as ltree) AND id != :childId
            """,
            nativeQuery = true)
    void syncReplyCountForAncestors(@Param("childPath") String childPath, @Param("childId") UUID childId, @Param("modifiedCount") int modifiedCount);
}
