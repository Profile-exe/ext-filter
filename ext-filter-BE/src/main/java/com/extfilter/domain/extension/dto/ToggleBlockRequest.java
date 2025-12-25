package com.extfilter.domain.extension.dto;

import jakarta.validation.constraints.NotNull;

public record ToggleBlockRequest(
        @NotNull(message = "차단 여부는 필수입니다")
        Boolean isBlocked
) {
}
