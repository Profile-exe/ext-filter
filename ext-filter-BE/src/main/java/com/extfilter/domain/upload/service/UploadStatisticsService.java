package com.extfilter.domain.upload.service;

import com.extfilter.domain.upload.dto.DailyUploadDto;
import com.extfilter.domain.upload.dto.ExtensionCountDto;
import com.extfilter.domain.upload.dto.StatisticsResponse;
import com.extfilter.domain.upload.entity.UploadStatus;
import com.extfilter.domain.upload.repository.UploadHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UploadStatisticsService {

    private final UploadHistoryRepository uploadHistoryRepository;

    private static final int TOP_BLOCKED_EXTENSIONS_LIMIT = 5;
    private static final int UPLOAD_TREND_DAYS = 7;

    public StatisticsResponse getOverview() {
        // 전체/성공/차단 개수
        long totalUploads = uploadHistoryRepository.count();
        long successCount = uploadHistoryRepository.countByUploadStatus(UploadStatus.SUCCESS);
        long blockedCount = uploadHistoryRepository.countByUploadStatus(UploadStatus.BLOCKED);

        // 차단율 계산 (전체가 0이면 0.0으로 반환)
        double blockingRate = totalUploads > 0
                ? (double) blockedCount / totalUploads * 100
                : 0.0;

        // 확장자별 차단 TOP 5
        List<ExtensionCountDto> topBlockedExtensions = uploadHistoryRepository.findTopBlockedExtensions(
                UploadStatus.BLOCKED,
                TOP_BLOCKED_EXTENSIONS_LIMIT
        );

        // 최근 7일 일별 추이
        LocalDateTime startDate = LocalDateTime.now().minusDays(UPLOAD_TREND_DAYS);
        List<DailyUploadDto> uploadTrend = uploadHistoryRepository.findDailyUploadTrend(startDate);

        return new StatisticsResponse(
                totalUploads,
                successCount,
                blockedCount,
                blockingRate,
                topBlockedExtensions,
                uploadTrend
        );
    }
}
