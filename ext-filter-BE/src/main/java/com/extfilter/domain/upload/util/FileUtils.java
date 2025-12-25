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

    public static String generateStoredFilename(String extension) {
        String uniqueId = UUID.randomUUID().toString();
        return uniqueId + "." + extension;
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
