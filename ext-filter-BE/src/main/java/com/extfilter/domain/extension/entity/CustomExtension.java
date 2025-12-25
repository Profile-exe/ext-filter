package com.extfilter.domain.extension.entity;

import com.extfilter.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "custom_extensions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomExtension extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "extension_name", nullable = false, unique = true, length = 20)
    private String extensionName;

    public static CustomExtension of(String extensionName) {
        CustomExtension extension = new CustomExtension();
        extension.extensionName = extensionName.toLowerCase();
        return extension;
    }
}
