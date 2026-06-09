package com.shibana.post_service.mapper;

import com.shibana.post_service.model.dto.response.AuthorResponse;
import com.shibana.post_service.model.dto.response.CommentResponse;
import com.shibana.post_service.model.entity.Author;
import com.shibana.post_service.model.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {
    @Value("${service.media.static-url}")
    String mediaStaticUrl;

    @Mapping(target = "author", expression = "java(toAuthorResponse(author))")
    public abstract CommentResponse toCommentResponse(Comment comment, Author author);

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "avatarUrl", source = "avatarMediaName", qualifiedByName = "toFullUrl")
    public abstract AuthorResponse toAuthorResponse(Author author);

    @Named("toFullUrl")
    protected String toFullUrl(String mediaName) {
        if (mediaName == null || mediaName.isEmpty()) {
            return null;
        }
        return mediaStaticUrl + mediaName;
    }
}
