package com.extfilter.common.exception;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

public record ErrorResponse(
        String code,
        Integer status,
        String message,
        @JsonInclude(NON_NULL) List<ValidationError> invalidParams) {

    public ErrorResponse(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public ErrorResponse(ErrorCode errorCode, List<ValidationError> invalidParams) {
        this(
                errorCode.getCode(),
                errorCode.getHttpStatus().value(),
                errorCode.getMessage(),
                invalidParams);
    }
}
