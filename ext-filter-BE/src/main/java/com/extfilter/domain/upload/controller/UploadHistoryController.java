package com.extfilter.domain.upload.controller;

import com.extfilter.domain.upload.dto.UploadHistoryResponse;
import com.extfilter.domain.upload.entity.UploadStatus;
import com.extfilter.domain.upload.service.UploadHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadHistoryController {

    private final UploadHistoryService uploadHistoryService;

    @GetMapping("/history")
    public ResponseEntity<Page<UploadHistoryResponse>> getHistory(
            @RequestParam(required = false) UploadStatus status,
            @RequestParam(required = false) String extension,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<UploadHistoryResponse> response = uploadHistoryService.getHistory(status, extension, pageable);
        return ResponseEntity.ok(response);
    }
}
