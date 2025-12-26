package com.extfilter.domain.upload.dto;

import com.extfilter.domain.upload.entity.UploadHistory;
import com.extfilter.domain.upload.entity.UploadStatus;

import java.time.LocalDateTime;

public record UploadHistoryResponse(
        Long id,
        String originalFilename,
        String fileExtension,
        Long fileSize,
        UploadStatus uploadStatus,
        String storedFilename,
        LocalDateTime createdAt
) {
    public static UploadHistoryResponse from(UploadHistory entity) {
        return new UploadHistoryResponse(
                entity.getId(),
                entity.getOriginalFilename(),
                entity.getFileExtension(),
                entity.getFileSize(),
                entity.getUploadStatus(),
                entity.getStoredFilename(),
                entity.getCreatedAt()
        );
    }
}
