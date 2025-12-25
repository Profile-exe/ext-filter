-- 커스텀 확장자 테이블 생성
-- 사용자 정의 확장자 (최대 200개, 추가 시 항상 차단됨)

CREATE TABLE custom_extensions (
    id BIGSERIAL PRIMARY KEY,
    extension_name VARCHAR(20) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
