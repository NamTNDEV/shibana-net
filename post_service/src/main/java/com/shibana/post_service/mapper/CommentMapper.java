package com.shibana.post_service.mapper;

import com.shibana.post_service.model.dto.response.CommentResponse;
import com.shibana.post_service.model.entity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentResponse  toCommentResponse(Comment comment);
}
