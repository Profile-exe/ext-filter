package com.extfilter.domain.extension.exception;

import com.extfilter.common.exception.BusinessException;

public class BlockedExtensionException extends BusinessException {

    public BlockedExtensionException() {
        super(ExtensionErrorCode.BLOCKED_EXTENSION);
    }
}
