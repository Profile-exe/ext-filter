-- 업로드 이력 테이블 생성
-- 모든 파일 업로드 시도 기록 (성공/차단 모두)

CREATE TYPE upload_status AS ENUM ('SUCCESS', 'BLOCKED');

CREATE TABLE upload_history (
    id BIGSERIAL PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    file_extension VARCHAR(20) NOT NULL,
    file_size BIGINT NOT NULL,
    upload_status upload_status NOT NULL,
    stored_filename VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
