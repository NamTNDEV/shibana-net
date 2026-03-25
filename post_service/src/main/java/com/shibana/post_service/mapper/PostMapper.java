package com.shibana.post_service.mapper;

import com.shibana.post_service.model.dto.response.PostResponse;
import com.shibana.post_service.model.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponse toPostResponse(Post post);
}
