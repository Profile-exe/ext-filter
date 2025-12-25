package com.extfilter.domain.extension.repository;

import com.extfilter.domain.extension.entity.FixedExtension;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixedExtensionRepository extends JpaRepository<FixedExtension, Long> {
}
