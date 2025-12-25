package com.extfilter.domain.extension.dto;

import com.extfilter.domain.extension.entity.CustomExtension;

import java.time.LocalDateTime;

public record CustomExtensionResponse(
        Long id,
        String extensionName,
        LocalDateTime createdAt
) {
    public static CustomExtensionResponse from(CustomExtension entity) {
        return new CustomExtensionResponse(
                entity.getId(),
                entity.getExtensionName(),
                entity.getCreatedAt()
        );
    }
}
