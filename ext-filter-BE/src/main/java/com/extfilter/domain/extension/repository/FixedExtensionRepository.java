package com.extfilter.domain.extension.repository;

import com.extfilter.domain.extension.entity.FixedExtension;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixedExtensionRepository extends JpaRepository<FixedExtension, Long> {

    Optional<FixedExtension> findByExtensionName(String extensionName);
}
