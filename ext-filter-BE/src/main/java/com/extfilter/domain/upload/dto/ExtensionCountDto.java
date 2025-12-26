package com.extfilter.domain.upload.dto;

import com.querydsl.core.annotations.QueryProjection;

public record ExtensionCountDto(
        String fileExtension,
        Long count
) {

    @QueryProjection
    public ExtensionCountDto {
    }
}
