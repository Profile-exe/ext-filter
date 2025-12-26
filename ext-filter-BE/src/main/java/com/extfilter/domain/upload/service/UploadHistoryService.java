package com.extfilter.domain.upload.service;

import com.extfilter.domain.upload.dto.UploadHistoryResponse;
import com.extfilter.domain.upload.entity.UploadHistory;
import com.extfilter.domain.upload.entity.UploadStatus;
import com.extfilter.domain.upload.repository.UploadHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UploadHistoryService {

    private final UploadHistoryRepository uploadHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveUploadHistory(UploadHistory uploadHistory) {
        uploadHistoryRepository.save(uploadHistory);
    }

    public Page<UploadHistoryResponse> getHistory(
            UploadStatus status,
            String extension,
            Pageable pageable
    ) {
        Page<UploadHistory> historyPage = uploadHistoryRepository.findHistoryWithFilters(
                status, extension, pageable
        );
        return historyPage.map(UploadHistoryResponse::from);
    }
}
