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
@Table(name = "fixed_extensions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FixedExtension extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "extension_name", nullable = false, unique = true, length = 20)
    private String extensionName;

    @Column(name = "is_blocked", nullable = false)
    private Boolean isBlocked;

    public void block() {
        this.isBlocked = true;
    }

    public void unblock() {
        this.isBlocked = false;
    }
}
