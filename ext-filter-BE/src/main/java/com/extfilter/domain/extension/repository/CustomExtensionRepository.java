package com.extfilter.domain.extension.repository;

import com.extfilter.domain.extension.entity.CustomExtension;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomExtensionRepository extends JpaRepository<CustomExtension, Long> {
}
