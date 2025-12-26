package com.extfilter.domain.upload.dto;

import java.util.List;

public record StatisticsResponse(
        Long totalUploads,
        Long successCount,
        Long blockedCount,
        Double blockingRate,
        List<ExtensionCountDto> topBlockedExtensions,
        List<DailyUploadDto> uploadTrend
) {
}
