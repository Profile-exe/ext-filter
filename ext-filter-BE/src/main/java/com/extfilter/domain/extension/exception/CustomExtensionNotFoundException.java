package com.extfilter.domain.extension.exception;

import com.extfilter.common.exception.BusinessException;

public class CustomExtensionNotFoundException extends BusinessException {

    public CustomExtensionNotFoundException() {
        super(ExtensionErrorCode.CUSTOM_EXTENSION_NOT_FOUND);
    }
}
