package com.extfilter.domain.extension.exception;

import com.extfilter.common.exception.BusinessException;

public class ExtensionLimitExceededException extends BusinessException {

    public ExtensionLimitExceededException() {
        super(ExtensionErrorCode.EXTENSION_LIMIT_EXCEEDED);
    }
}
