package com.extfilter.domain.extension.exception;

import com.extfilter.common.exception.BusinessException;

public class FixedExtensionNotFoundException extends BusinessException {

    public FixedExtensionNotFoundException() {
        super(ExtensionErrorCode.FIXED_EXTENSION_NOT_FOUND);
    }
}
