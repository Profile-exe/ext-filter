package com.extfilter.domain.upload.exception;

import com.extfilter.common.exception.BusinessException;

public class FileStorageFailureException extends BusinessException {

    public FileStorageFailureException() {
        super(UploadErrorCode.FILE_STORAGE_FAILURE);
    }
}
