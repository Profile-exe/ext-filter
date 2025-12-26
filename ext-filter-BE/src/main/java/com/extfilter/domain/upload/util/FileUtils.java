package com.extfilter.domain.upload.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class FileUtils {

    public static String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "unnamed";
        }
        // Path Traversal 공격 패턴 제거 (../, ..\, /, \)
        return filename.replaceAll("[/\\\\]", "")
                .replace("\\.\\.", "")
                .trim();
    }

    public static String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    public static String removeExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "file";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(0, lastDotIndex);
        }
        return filename;
    }

    public static String generateStoredFilename(String originalFilename, String extension) {
        // 1. 원본 파일명 살균
        String sanitized = sanitizeFilename(originalFilename);

        // 2. 확장자 제거
        String baseFilename = removeExtension(sanitized);

        // 3. 빈 값 처리
        if (baseFilename.isBlank()) {
            baseFilename = "file";
        }

        // 4. 긴 파일명 자르기 (최대 100자)
        if (baseFilename.length() > 100) {
            baseFilename = baseFilename.substring(0, 100);
        }

        // 5. UUID 생성
        String uniqueId = UUID.randomUUID().toString();

        // 6. 최종 파일명 생성
        if (extension == null || extension.isBlank()) {
            return baseFilename + "_" + uniqueId;
        }
        return baseFilename + "_" + uniqueId + "." + extension;
    }

    public static Path buildFilePath(String baseDir, String filename) {
        String datePath = getDateBasedPath(baseDir);
        return Paths.get(datePath).toAbsolutePath().resolve(filename);
    }

    private static String getDateBasedPath(String baseDir) {
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String day = String.format("%02d", now.getDayOfMonth());
        return Paths.get(baseDir, year, month, day).toString();
    }
}
