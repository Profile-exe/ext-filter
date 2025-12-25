package com.extfilter.domain.upload.exception;

import com.extfilter.common.exception.BusinessException;

public class FileSizeLimitExceededException extends BusinessException {

    public FileSizeLimitExceededException() {
        super(UploadErrorCode.FILE_SIZE_LIMIT_EXCEEDED);
    }
}
