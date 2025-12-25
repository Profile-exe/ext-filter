package com.extfilter.domain.extension.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomExtensionRequest(
        @NotBlank(message = "확장자명은 필수입니다")
        @Pattern(regexp = "^[a-zA-Z0-9]{1,20}$", message = "확장자는 영숫자 1-20자여야 합니다")
        String extensionName
) {
}
