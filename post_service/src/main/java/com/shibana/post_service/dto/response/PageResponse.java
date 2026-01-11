package com.shibana.post_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PageResponse<T>(
        List<T> payload,
        int page,
        int size,
        Boolean hasNext,
        Long totalElements,
        Integer totalPages
) {
}
