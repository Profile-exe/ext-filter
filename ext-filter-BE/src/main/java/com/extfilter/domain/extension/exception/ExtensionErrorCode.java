package com.extfilter.domain.extension.exception;

import com.extfilter.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExtensionErrorCode implements ErrorCode {
    DUPLICATE_EXTENSION("EXT001", HttpStatus.CONFLICT, "이미 존재하는 확장자입니다."),
    EXTENSION_LIMIT_EXCEEDED("EXT002", HttpStatus.FORBIDDEN, "커스텀 확장자는 최대 200개까지 추가할 수 있습니다."),
    FIXED_EXTENSION_NOT_FOUND("EXT003", HttpStatus.NOT_FOUND, "고정 확장자를 찾을 수 없습니다."),
    CUSTOM_EXTENSION_NOT_FOUND("EXT004", HttpStatus.NOT_FOUND, "커스텀 확장자를 찾을 수 없습니다."),
    BLOCKED_EXTENSION("EXT005", HttpStatus.FORBIDDEN, "차단된 확장자입니다."),
    ;

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    ExtensionErrorCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
