package com.extfilter.domain.upload.dto;

import java.time.LocalDateTime;

public record FileUploadResponse(
        String originalFileName,
        String storedFileName,
        String fileExtension,
        Long fileSize,
        LocalDateTime uploadedAt
) {

    public static FileUploadResponse of(
            String originalFileName,
            String storedFileName,
            String fileExtension,
            Long fileSize,
            LocalDateTime uploadedAt
    ) {
        return new FileUploadResponse(
                originalFileName,
                storedFileName,
                fileExtension,
                fileSize,
                uploadedAt
        );
    }
}
