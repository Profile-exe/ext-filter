package com.extfilter.domain.extension.dto;

import com.extfilter.domain.extension.entity.FixedExtension;

import java.time.LocalDateTime;

public record FixedExtensionResponse(
        Long id,
        String extensionName,
        Boolean isBlocked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FixedExtensionResponse from(FixedExtension entity) {
        return new FixedExtensionResponse(
                entity.getId(),
                entity.getExtensionName(),
                entity.getIsBlocked(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
