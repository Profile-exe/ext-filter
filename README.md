# 파일 확장자 차단 서비스

서버 보안에 위협이 되는 특정 파일 확장자(exe, sh 등)의 첨부 및 전송을 제한하는 웹 서비스입니다.

## 배포 URL

- **Frontend**: https://ext-filter.vercel.app/
- **Backend(Swagger)**: https://quattro-cheese.duckdns.org/swagger-ui/index.html

---

## 주요 기능

### 필수 구현 기능

#### 1. 고정 확장자 관리

- **대상 확장자**: 7개 (bat, cmd, com, cpl, exe, scr, js)
- **체크박스 토글**: 각 확장자의 차단 여부를 개별적으로 설정 가능
- **상태 유지**: 설정한 차단 상태가 데이터베이스에 저장되어 새로고침 시에도 유지
- **초기 상태**: 모든 고정 확장자는 체크 해제(차단하지 않음) 상태로 시작

#### 2. 커스텀 확장자 관리

- **추가 기능**: 사용자가 원하는 확장자를 직접 추가 가능 (최대 200개)
- **삭제 기능**: 등록된 확장자 옆 버튼 클릭으로 삭제
- **개수 표시**: 현재 등록된 커스텀 확장자 개수 표시 (예: 3/200)
- **입력 제한**:
  - 확장자 형식: 영문자와 숫자만 허용
  - 최대 길이: 20자
  - 중복 방지: 이미 등록된 확장자는 추가 불가

#### 3. 파일 업로드 및 차단

- **차단 검증**: 업로드 시도 시 고정 확장자 및 커스텀 확장자와 비교하여 차단
- **파일 저장**: 허용된 파일은 서버에 안전하게 저장
- **파일 크기 제한**: 최대 10MB
- **업로드 결과**: 성공/차단 여부 조회

#### 4. 예외 처리

- **중복 확장자 방지**: 고정 확장자와 동일한 커스텀 확장자 추가 불가
- **입력 검증**: 특수문자 등 유효하지 않은 입력에 대한 에러 메시지 표시
- **200개 제한**: 커스텀 확장자가 200개를 초과하면 추가 불가

### 추가 구현 기능

#### 5. 업로드 이력 관리

- **전체 이력 추적**: 성공 및 차단된 모든 업로드 기록 저장
- **필터링 기능**: 업로드 상태(전체/성공/차단) 및 확장자별 필터링
- **페이지네이션**: 이력 목록을 페이지 단위로 조회
- **Excel 내보내기**: 업로드 이력을 엑셀 파일로 다운로드 가능
- **상세 정보**: 파일명, 확장자, 크기, 업로드 일시 등 표시

#### 6. 통계 대시보드

- **통계 카드**: 전체 업로드 수, 성공 수, 차단 수, 차단율 표시
- **TOP 5 차단 확장자**: 가장 많이 차단된 확장자를 막대 차트로 시각화
- **업로드 추이**: 최근 7일간의 일별 업로드 추이를 라인 차트로 표시

#### 7. 기타 구현 사항

- **도메인 분리**: 확장자 관리와 파일 업로드를 별도 도메인으로 분리
- **QueryDSL**: 동적 쿼리(필터링, 정렬) 구현
- **Flyway**: 데이터베이스 스키마 버전 관리 및 마이그레이션
- **Global Exception Handler**: 전역 예외처리
- **JPA Auditing**: 생성/수정 시간 자동 관리
- **Swagger**: API 문서 자동 생성

---

## 기술 스택

**Backend**

- Spring Boot, Java 17, Spring Data JPA, QueryDSL, Flyway
- PostgreSQL, Lombok, Swagger

**Frontend**

- React, Vite, React Router, Tailwind CSS
- Recharts, Axios, xlsx-js-style

**Infrastructure**

- Docker Compose, Vercel, AWS EC2

---

## 프로젝트 구조

```
ext-filter/
├── ext-filter-BE/              # Spring Boot Backend
│   ├── src/main/java/
│   │   └── com/extfilter/
│   │       ├── domain/         # extension, upload 도메인
│   │       └── common/         # 공통 설정 및 예외 처리
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/       # Flyway 마이그레이션 스크립트
│   ├── compose.yaml
│   └── build.gradle
│
└── ext-filter-FE/              # React Frontend
    ├── src/
    │   ├── pages/              # 4개 메인 페이지
    │   ├── components/         # 재사용 컴포넌트
    │   ├── services/           # API 호출
    │   ├── hooks/              # Custom Hooks
    │   └── utils/              # 유틸리티
    ├── vercel.json
    └── package.json
```

---

## 로컬 개발 환경 설정

### 사전 요구사항

- **Java 17** 이상
- **Node.js 18** 이상
- **Docker & Docker Compose**

### Backend 실행

#### 1. PostgreSQL 실행 (Docker Compose)

```bash
cd ext-filter-BE
docker compose up -d
```

#### 2. 데이터베이스 연결 정보 확인

```
Database: extfilter
User: myuser
Password: secret
Port: 5432
```

#### 3. Spring Boot 애플리케이션 실행

```bash
./gradlew bootRun
```

#### 4. 실행 확인

- **API 서버**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html

Flyway가 자동으로 데이터베이스 스키마를 생성하고, 7개의 고정 확장자를 포함한 초기 데이터를 삽입

### Frontend 실행

#### 1. 의존성 설치

```bash
cd ext-filter-FE
npm install
```

#### 2. 환경 변수 설정

프로젝트 루트에 `.env` 파일을 생성하고 다음 내용을 추가:

```env
VITE_API_BASE_URL=http://localhost:8080
```

#### 3. 개발 서버 실행

```bash
npm run dev
```

#### 4. 실행 확인

- **Frontend 애플리케이션**: http://localhost:5173

### 기능 테스트 시나리오

1. **확장자 관리 페이지** (`/` 또는 `/extensions`)

   - 고정 확장자 중 `exe`를 체크하여 차단 설정
   - 커스텀 확장자 추가: `pdf` 입력 후 추가

2. **파일 업로드 페이지** (`/upload`)

   - exe 파일 업로드 시도 → 차단 메시지 확인
   - txt 파일 업로드 시도 → 성공 메시지 확인

3. **업로드 이력 페이지** (`/history`)

   - 차단된 exe 파일 기록 확인
   - 필터링: "차단됨" 선택 → exe 파일만 표시
   - Excel 내보내기 버튼 클릭 → 엑셀 파일 다운로드

4. **통계 페이지** (`/statistics`)
   - 전체 업로드 수, 차단 수, 차단율 확인
   - TOP 5 차단 확장자 차트에서 `exe` 확인
   - 최근 7일 업로드 추이 확인

---

## API 문서

### Swagger UI

배포된 대화형 API 문서: **https://quattro-cheese.duckdns.org/swagger-ui/index.html**

### 주요 엔드포인트

#### 확장자 관리

| Method   | Endpoint                                  | 설명                       |
| -------- | ----------------------------------------- | -------------------------- |
| `GET`    | `/api/extensions/fixed`                   | 고정 확장자 목록 조회      |
| `PUT`    | `/api/extensions/fixed/{extension-name}`  | 고정 확장자 차단 여부 토글 |
| `GET`    | `/api/extensions/custom`                  | 커스텀 확장자 목록 조회    |
| `POST`   | `/api/extensions/custom`                  | 커스텀 확장자 추가         |
| `DELETE` | `/api/extensions/custom/{extension-id}`   | 커스텀 확장자 삭제         |

#### 파일 업로드

| Method | Endpoint           | 설명                              |
| ------ | ------------------ | --------------------------------- |
| `POST` | `/api/uploads/file` | 파일 업로드 (multipart/form-data) |

#### 업로드 이력

| Method | Endpoint               | 설명                                    |
| ------ | ---------------------- | --------------------------------------- |
| `GET`  | `/api/uploads/history` | 업로드 이력 조회 (페이지네이션, 필터링) |

**쿼리 파라미터**:

- `status`: 업로드 상태 (SUCCESS, BLOCKED)
- `extension`: 확장자 필터링
- `page`: 페이지 번호 (0부터 시작)
- `size`: 페이지 크기

#### 통계

| Method | Endpoint                   | 설명                      |
| ------ | -------------------------- | ------------------------- |
| `GET`  | `/api/statistics/overview` | 통계 대시보드 데이터 조회 |

---

### 추가 고려사항

#### 1. Path Traversal 공격 방지

```java
// 파일명에서 경로 구분자 제거
String sanitizedFilename = filename.replaceAll("[/\\\\]", "");
```

#### 2. UUID 기반 파일 저장

```java
// 충돌 방지 및 경로 노출 방지
String storedFilename = UUID.randomUUID() + "_" + sanitizedFilename;
```

#### 3. 입력 검증

- 확장자 형식: 정규표현식으로 영문자와 숫자만 허용 (`^[a-zA-Z0-9]+$`)
- 중복 검증: 고정 확장자 및 커스텀 확장자 테이블 모두 확인
- 길이 제한: 데이터베이스 컬럼 제약 + 애플리케이션 레벨 검증

#### 4. CORS 설정

```java
// Frontend 도메인만 허용
@CrossOrigin(origins = "https://ext-filter.vercel.app")
```

### 예외 처리 전략

#### Custom Exception 클래스

- `DuplicateExtensionException`: 중복 확장자 추가 시도 (409 Conflict)
- `ExtensionLimitExceededException`: 200개 제한 초과 (422 Unprocessable Entity)
- `InvalidExtensionFormatException`: 유효하지 않은 확장자 형식 (400 Bad Request)
- `BlockedExtensionException`: 차단된 파일 업로드 시도 (400 Bad Request)
- `FileSizeLimitExceededException`: 파일 크기 초과 (413 Payload Too Large)

#### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateExtensionException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateExtensionException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("DUPLICATE_EXTENSION", e.getMessage()));
    }
    // ...
}
```

### 데이터 관리

#### Flyway Migration

- **V1**: `fixed_extensions` 테이블 생성
- **V2**: `custom_extensions` 테이블 생성
- **V3**: `upload_history` 테이블 생성
- **V4**: 7개 고정 확장자 초기 데이터 삽입 (모두 `is_blocked = false`)
- **V5**: 샘플 업로드 이력 데이터 삽입 (시각화용)

#### JPA Auditing

```java
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

- 모든 엔티티의 생성/수정 시간 자동으로 관리

### 파일 저장 전략

#### 디렉토리 구조

```
uploads/
├── 2025/
│   └── 12/
│       └── 26/
│           ├── uuid1_document.pdf
│           ├── uuid2_image.png
│           └── uuid3_report.docx
```

- UUID 접두사로 동일 파일명 업로드 가능
- 날짜별 폴더로 파일 관리

### 프론트엔드 UX

#### 드래그 앤 드롭 파일 업로드

- `onDragEnter`, `onDragOver`, `onDrop` 이벤트 처리
- 드래그 상태에 따른 시각적 피드백 (테두리 색상 변경)

#### Recharts 시각화

- 가장 많이 차단된 확장자 TOP 5
- 최근 7일 업로드 추이
