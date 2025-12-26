package com.extfilter.domain.upload.repository;

import com.extfilter.domain.upload.dto.DailyUploadDto;
import com.extfilter.domain.upload.dto.ExtensionCountDto;
import com.extfilter.domain.upload.entity.UploadHistory;
import com.extfilter.domain.upload.entity.UploadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface UploadHistoryRepositoryCustom {

    Page<UploadHistory> findHistoryWithFilters(
            UploadStatus status,
            String extension,
            Pageable pageable
    );

    List<ExtensionCountDto> findTopBlockedExtensions(
            UploadStatus status,
            int limit
    );

    List<DailyUploadDto> findDailyUploadTrend(
            LocalDateTime startDate
    );
}
