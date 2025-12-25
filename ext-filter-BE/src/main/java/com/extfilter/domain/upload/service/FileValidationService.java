package com.extfilter.domain.upload.service;

import com.extfilter.domain.upload.exception.FileSizeLimitExceededException;
import org.springframework.stereotype.Service;

@Service
public class FileValidationService {

    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024L; // 10MB

    public void validateFileSize(long fileSize) {
        if (fileSize > MAX_FILE_SIZE_BYTES) {
            throw new FileSizeLimitExceededException();
        }
    }
}
