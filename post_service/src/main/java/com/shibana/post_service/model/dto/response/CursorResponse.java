package com.shibana.post_service.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CursorResponse<T>(
        int size,
        Boolean hasNext,
        String nextCursor,
        List<T> payload
) {
}

