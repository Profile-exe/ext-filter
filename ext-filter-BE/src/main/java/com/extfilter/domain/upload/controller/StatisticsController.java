package com.extfilter.domain.upload.controller;

import com.extfilter.domain.upload.dto.StatisticsResponse;
import com.extfilter.domain.upload.service.UploadStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final UploadStatisticsService uploadStatisticsService;

    @GetMapping("/overview")
    public ResponseEntity<StatisticsResponse> getOverview() {
        StatisticsResponse response = uploadStatisticsService.getOverview();
        return ResponseEntity.ok(response);
    }
}
