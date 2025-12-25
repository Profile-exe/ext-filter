package com.extfilter.domain.extension.controller;

import com.extfilter.domain.extension.dto.CustomExtensionRequest;
import com.extfilter.domain.extension.dto.CustomExtensionResponse;
import com.extfilter.domain.extension.dto.FixedExtensionResponse;
import com.extfilter.domain.extension.dto.ToggleBlockRequest;
import com.extfilter.domain.extension.service.ExtensionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/extensions")
@RequiredArgsConstructor
public class ExtensionController {

    private final ExtensionService extensionService;

    @GetMapping("/fixed")
    public ResponseEntity<List<FixedExtensionResponse>> getFixedExtensions() {
        List<FixedExtensionResponse> response = extensionService.getFixedExtensions();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/fixed/{extension-name}")
    public ResponseEntity<FixedExtensionResponse> toggleFixedExtension(
            @PathVariable(name = "extension-name") String extensionName,
            @Valid @RequestBody ToggleBlockRequest request
    ) {
        FixedExtensionResponse response = extensionService.toggleFixedExtension(
                extensionName,
                request.isBlocked()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/custom")
    public ResponseEntity<List<CustomExtensionResponse>> getCustomExtensions() {
        List<CustomExtensionResponse> response = extensionService.getCustomExtensions();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/custom")
    public ResponseEntity<CustomExtensionResponse> addCustomExtension(
            @Valid @RequestBody CustomExtensionRequest request
    ) {
        CustomExtensionResponse response = extensionService.addCustomExtension(request.extensionName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/custom/{extension-id}")
    public ResponseEntity<Void> deleteCustomExtension(
            @PathVariable(name = "extension-id") Long extensionId
    ) {
        extensionService.deleteCustomExtension(extensionId);
        return ResponseEntity.noContent().build();
    }
}
