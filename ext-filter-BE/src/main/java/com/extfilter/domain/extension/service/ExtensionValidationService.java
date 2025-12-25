package com.extfilter.domain.extension.service;

import com.extfilter.domain.extension.exception.DuplicateExtensionException;
import com.extfilter.domain.extension.exception.ExtensionLimitExceededException;
import com.extfilter.domain.extension.repository.CustomExtensionRepository;
import com.extfilter.domain.extension.repository.FixedExtensionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExtensionValidationService {

    private static final int MAX_CUSTOM_EXTENSIONS = 200;

    private final FixedExtensionRepository fixedExtensionRepository;
    private final CustomExtensionRepository customExtensionRepository;

    public void validateCustomLimit() {
        long count = customExtensionRepository.count();
        if (count >= MAX_CUSTOM_EXTENSIONS) {
            throw new ExtensionLimitExceededException();
        }
    }

    public void validateNotDuplicate(String extensionName) {
        String normalizedName = extensionName.toLowerCase();

        // 고정 확장자와 중복 체크
        boolean existsInFixed = fixedExtensionRepository.findByExtensionName(normalizedName).isPresent();
        if (existsInFixed) {
            throw new DuplicateExtensionException();
        }

        // 커스텀 확장자와 중복 체크
        boolean existsInCustom = customExtensionRepository.existsByExtensionName(normalizedName);
        if (existsInCustom) {
            throw new DuplicateExtensionException();
        }
    }
}
