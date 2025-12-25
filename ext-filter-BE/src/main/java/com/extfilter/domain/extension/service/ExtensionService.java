package com.extfilter.domain.extension.service;

import com.extfilter.domain.extension.dto.CustomExtensionResponse;
import com.extfilter.domain.extension.dto.FixedExtensionResponse;
import com.extfilter.domain.extension.entity.CustomExtension;
import com.extfilter.domain.extension.entity.FixedExtension;
import com.extfilter.domain.extension.exception.CustomExtensionNotFoundException;
import com.extfilter.domain.extension.exception.FixedExtensionNotFoundException;
import com.extfilter.domain.extension.repository.CustomExtensionRepository;
import com.extfilter.domain.extension.repository.FixedExtensionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExtensionService {

    private final FixedExtensionRepository fixedExtensionRepository;
    private final CustomExtensionRepository customExtensionRepository;
    private final ExtensionValidationService validationService;

    public List<FixedExtensionResponse> getFixedExtensions() {
        return fixedExtensionRepository.findAll()
                .stream()
                .map(FixedExtensionResponse::from)
                .toList();
    }

    @Transactional
    public FixedExtensionResponse toggleFixedExtension(String extensionName, boolean isBlocked) {
        String normalizedName = extensionName.toLowerCase();

        FixedExtension extension = fixedExtensionRepository.findByExtensionName(normalizedName)
                .orElseThrow(FixedExtensionNotFoundException::new);

        if (isBlocked) {
            extension.block();
        } else {
            extension.unblock();
        }

        return FixedExtensionResponse.from(extension);
    }

    public List<CustomExtensionResponse> getCustomExtensions() {
        return customExtensionRepository.findAll()
                .stream()
                .map(CustomExtensionResponse::from)
                .toList();
    }

    @Transactional
    public CustomExtensionResponse addCustomExtension(String extensionName) {
        validationService.validateCustomLimit();
        validationService.validateNotDuplicate(extensionName);

        CustomExtension extension = CustomExtension.of(extensionName);
        CustomExtension saved = customExtensionRepository.save(extension);

        return CustomExtensionResponse.from(saved);
    }

    @Transactional
    public void deleteCustomExtension(Long extensionId) {
        CustomExtension extension = customExtensionRepository.findById(extensionId)
                .orElseThrow(CustomExtensionNotFoundException::new);

        customExtensionRepository.delete(extension);
    }
}
