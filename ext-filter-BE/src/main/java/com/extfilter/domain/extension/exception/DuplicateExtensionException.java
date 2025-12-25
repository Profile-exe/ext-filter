package com.extfilter.domain.extension.exception;

import com.extfilter.common.exception.BusinessException;

public class DuplicateExtensionException extends BusinessException {

    public DuplicateExtensionException() {
        super(ExtensionErrorCode.DUPLICATE_EXTENSION);
    }
}
