package com.extfilter.domain.upload.service;

import com.extfilter.domain.extension.exception.BlockedExtensionException;
import com.extfilter.domain.extension.service.ExtensionValidationService;
import com.extfilter.domain.upload.dto.FileUploadResponse;
import com.extfilter.domain.upload.entity.UploadHistory;
import com.extfilter.domain.upload.exception.FileStorageFailureException;
import com.extfilter.domain.upload.util.FileUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileUploadService {

    private static final String UPLOAD_DIR = "uploads";

    private final ExtensionValidationService extensionValidationService;
    private final FileValidationService fileValidationService;
    private final UploadHistoryService uploadHistoryService;

    @Transactional
    public FileUploadResponse uploadFile(MultipartFile file) {
        String originalFilename = FileUtils.sanitizeFilename(file.getOriginalFilename());
        String extension = FileUtils.extractExtension(file.getOriginalFilename());
        boolean isBlocked = extensionValidationService.isBlocked(extension);

        fileValidationService.validateFileSize(file.getSize());

        if (isBlocked) {
            handleBlockedExtension(file, originalFilename, extension);
        }

        return handleNonBlockedExtension(file, originalFilename, extension);
    }

    private void handleBlockedExtension(MultipartFile file, String originalFilename, String extension) {
        UploadHistory blocked = UploadHistory.blocked(originalFilename, extension, file.getSize());
        uploadHistoryService.saveUploadHistory(blocked);
        throw new BlockedExtensionException();
    }

    private FileUploadResponse handleNonBlockedExtension(MultipartFile file, String originalFilename, String extension) {
        String storedFilename = FileUtils.generateStoredFilename(extension);
        String storagePath = saveFile(file, storedFilename);

        UploadHistory success = UploadHistory.success(originalFilename, extension, file.getSize(), storagePath);
        uploadHistoryService.saveUploadHistory(success);

        return FileUploadResponse.of(
                originalFilename,
                storedFilename,
                extension,
                file.getSize(),
                success.getCreatedAt()
        );
    }

    private String saveFile(MultipartFile file, String storedFilename) {
        Path filePath = FileUtils.buildFilePath(UPLOAD_DIR, storedFilename);
        Path uploadPath = filePath.getParent();

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            file.transferTo(filePath.toFile());
            return filePath.toString();
        } catch (IOException e) {
            throw new FileStorageFailureException();
        }
    }
}
