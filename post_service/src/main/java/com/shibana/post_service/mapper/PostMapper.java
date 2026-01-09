package com.shibana.post_service.mapper;

import com.shibana.post_service.dto.response.PostResponse;
import com.shibana.post_service.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponse toPostResponse(Post post);
}
