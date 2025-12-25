package com.extfilter.domain.upload.service;

import com.extfilter.domain.upload.entity.UploadHistory;
import com.extfilter.domain.upload.repository.UploadHistoryRepository;
import lombok.RequiredArgsConstructor;
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
}
