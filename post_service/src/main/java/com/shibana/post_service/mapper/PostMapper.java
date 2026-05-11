package com.shibana.post_service.mapper;

import com.shibana.post_service.model.dto.response.PostResponse;
import com.shibana.post_service.model.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = "spring")
public abstract class PostMapper {
    @Value("${service.media.static-url}")
    String mediaStaticUrl;

//    @Mapping(target = "author.avatarMediaName", source = "author.avatarMediaName", qualifiedByName = "toFullUrl")
    public abstract PostResponse toPostResponse(Post post);

    @Named("toFullUrl")
    protected String toFullUrl(String mediaName) {
        if (mediaName == null || mediaName.isEmpty()) {
            return null;
        }
        return mediaStaticUrl + mediaName;
    }
}
