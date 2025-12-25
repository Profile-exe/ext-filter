package com.extfilter.domain.upload.exception;

import com.extfilter.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UploadErrorCode implements ErrorCode {
    FILE_STORAGE_FAILURE("UPL001", HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다."),
    FILE_SIZE_LIMIT_EXCEEDED("UPL002", HttpStatus.PAYLOAD_TOO_LARGE, "파일 크기는 10MB를 초과할 수 없습니다.")
    ;

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    UploadErrorCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
