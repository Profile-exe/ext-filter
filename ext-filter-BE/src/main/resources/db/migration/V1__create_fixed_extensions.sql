-- 고정 확장자 테이블 생성
-- 시스템에서 정의한 7개의 고정 확장자 (추가/삭제 불가, 차단 여부만 토글 가능)

CREATE TABLE fixed_extensions (
    id BIGSERIAL PRIMARY KEY,
    extension_name VARCHAR(20) NOT NULL UNIQUE,
    is_blocked BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
