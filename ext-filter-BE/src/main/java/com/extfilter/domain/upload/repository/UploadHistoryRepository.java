package com.extfilter.domain.upload.repository;

import com.extfilter.domain.upload.entity.UploadHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadHistoryRepository extends JpaRepository<UploadHistory, Long> {
}
