package com.shibana.post_service.mapper;

import com.shibana.post_service.model.dto.response.AuthorResponse;
import com.shibana.post_service.model.dto.response.PostResponse;
import com.shibana.post_service.model.entity.Author;
import com.shibana.post_service.model.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = "spring")
public abstract class PostMapper {
    @Value("${service.media.static-url}")
    String mediaStaticUrl;

    @Mapping(target = "author", ignore = true)
    public abstract PostResponse toPostResponse(Post post);

    @Mapping(target = "createdAt", source = "post.createdAt")
    @Mapping(target = "author", expression = "java(toAuthorResponse(author))")
    public abstract PostResponse toPostResponse(Post post, Author author);

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
