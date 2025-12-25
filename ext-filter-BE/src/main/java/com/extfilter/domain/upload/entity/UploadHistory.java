package com.extfilter.domain.upload.entity;

import com.extfilter.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "upload_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "file_extension", nullable = false, length = 20)
    private String fileExtension;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false)
    private UploadStatus uploadStatus;

    @Column(name = "stored_filename")
    private String storedFilename;

    // 파일 업로드 성공
    public static UploadHistory success(String originalFilename, String fileExtension,
                                        Long fileSize, String storedFilename) {
        UploadHistory history = new UploadHistory();
        history.originalFilename = originalFilename;
        history.fileExtension = fileExtension;
        history.fileSize = fileSize;
        history.uploadStatus = UploadStatus.SUCCESS;
        history.storedFilename = storedFilename;
        return history;
    }

    // 파일 업로드 차단
    public static UploadHistory blocked(String originalFilename, String fileExtension,
                                        Long fileSize) {
        UploadHistory history = new UploadHistory();
        history.originalFilename = originalFilename;
        history.fileExtension = fileExtension;
        history.fileSize = fileSize;
        history.uploadStatus = UploadStatus.BLOCKED;
        return history;
    }
}
