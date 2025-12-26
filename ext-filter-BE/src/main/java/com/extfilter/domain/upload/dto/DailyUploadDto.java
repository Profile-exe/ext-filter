package com.extfilter.domain.upload.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DailyUploadDto(
        LocalDate date,
        Long totalCount,
        Long successCount,
        Long blockedCount
) {

    @QueryProjection
    public DailyUploadDto(LocalDateTime dateTime, Long totalCount, Long successCount, Long blockedCount) {
        this(dateTime.toLocalDate(), totalCount, successCount, blockedCount);
    }
}
