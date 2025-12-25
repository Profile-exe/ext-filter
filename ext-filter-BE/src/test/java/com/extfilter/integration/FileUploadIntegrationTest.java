package com.extfilter.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.extfilter.common.exception.ErrorResponse;
import com.extfilter.domain.extension.dto.CustomExtensionRequest;
import com.extfilter.domain.extension.dto.ToggleBlockRequest;
import com.extfilter.domain.extension.exception.ExtensionErrorCode;
import com.extfilter.domain.extension.repository.CustomExtensionRepository;
import com.extfilter.domain.upload.dto.FileUploadResponse;
import com.extfilter.domain.upload.entity.UploadHistory;
import com.extfilter.domain.upload.entity.UploadStatus;
import com.extfilter.domain.upload.repository.UploadHistoryRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FileUploadIntegrationTest {

    private static final String UPLOAD_DIR = "uploads";

    @LocalServerPort
    private int port;

    @Autowired
    private UploadHistoryRepository uploadHistoryRepository;

    @Autowired
    private CustomExtensionRepository customExtensionRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        uploadHistoryRepository.deleteAll();
        customExtensionRepository.deleteAll();
    }

    @AfterEach
    void tearDown() throws IOException {
        // 테스트 후 업로드 디렉토리 정리
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (Files.exists(uploadPath)) {
            Files.walk(uploadPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @ParameterizedTest(name = "파일 업로드 성공: {0}")
    @CsvSource(value = {
            "test.txt,txt",
            "document.pdf,pdf",
            "image.jpg,jpg",
            "data.json,json"
    })
    void 파일_업로드_성공(String filename, String extension) throws IOException {
        // given
        File testFile = createTempFile(filename, "test content");

        // when
        FileUploadResponse response = given()
                .multiPart("file", testFile, "application/octet-stream")
                .when()
                .post("/api/uploads/file")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(FileUploadResponse.class);

        // then
        assertThat(response.originalFileName()).isNotNull();
        assertThat(response.fileExtension()).isEqualTo(extension);
        assertThat(response.storedFileName()).isNotNull();
        assertThat(response.storedFileName()).endsWith("." + extension);
        assertThat(response.fileSize()).isGreaterThan(0L);
        assertThat(response.uploadedAt()).isNotNull();

        // 업로드 히스토리 확인
        List<UploadHistory> histories = uploadHistoryRepository.findAll();
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getUploadStatus()).isEqualTo(UploadStatus.SUCCESS);
        assertThat(histories.get(0).getFileExtension()).isEqualTo(extension);

        testFile.delete();
    }

    @Test
    void 차단된_고정_확장자로_업로드_차단() throws IOException {
        // exe 확장자를 차단 상태로 설정
        given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(true))
                .when()
                .put("/api/extensions/fixed/exe");

        File testFile = createTempFile("malware.exe", "malicious content");

        ErrorResponse error = given()
                .multiPart("file", testFile, "application/octet-stream")
                .when()
                .post("/api/uploads/file")
                .then()
                .statusCode(ExtensionErrorCode.BLOCKED_EXTENSION.getHttpStatus().value())
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.code()).isEqualTo(ExtensionErrorCode.BLOCKED_EXTENSION.getCode());
        assertThat(error.message()).isEqualTo(ExtensionErrorCode.BLOCKED_EXTENSION.getMessage());

        // 차단 히스토리 확인
        List<UploadHistory> histories = uploadHistoryRepository.findAll();
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getUploadStatus()).isEqualTo(UploadStatus.BLOCKED);
        assertThat(histories.get(0).getFileExtension()).isEqualTo("exe");

        testFile.delete();
    }

    @Test
    void 차단된_커스텀_확장자로_업로드_차단() throws IOException {
        given()
                .contentType(ContentType.JSON)
                .body(new CustomExtensionRequest("danger"))
                .post("/api/extensions/custom");

        File testFile = createTempFile("virus.danger", "dangerous content");

        ErrorResponse error = given()
                .multiPart("file", testFile, "application/octet-stream")
                .when()
                .post("/api/uploads/file")
                .then()
                .statusCode(ExtensionErrorCode.BLOCKED_EXTENSION.getHttpStatus().value())
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.code()).isEqualTo(ExtensionErrorCode.BLOCKED_EXTENSION.getCode());

        // 차단 히스토리 확인
        List<UploadHistory> histories = uploadHistoryRepository.findAll();
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getUploadStatus()).isEqualTo(UploadStatus.BLOCKED);
        assertThat(histories.get(0).getFileExtension()).isEqualTo("danger");

        testFile.delete();
    }

    @Test
    void 날짜_기반_디렉토리_구조로_파일_저장() throws IOException {
        File testFile = createTempFile("test.txt", "content");

        given()
                .multiPart("file", testFile, "text/plain")
                .when()
                .post("/api/uploads/file")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        List<UploadHistory> histories = uploadHistoryRepository.findAll();
        String storedPath = histories.get(0).getStoredFilename();

        LocalDate now = LocalDate.now();
        String expectedPathPattern = String.format("%s%s%d%s%02d%s%02d",
                UPLOAD_DIR,
                File.separator,
                now.getYear(),
                File.separator,
                now.getMonthValue(),
                File.separator,
                now.getDayOfMonth()
        );

        assertThat(storedPath).contains(expectedPathPattern);

        testFile.delete();
    }

    @Test
    void Path_Traversal_공격_방지() throws IOException {
        File tempFile = createTempFile("test.txt", "content");

        File maliciousFile = new File(tempFile.getParent(), "../etcpasswd.txt");
        Files.move(tempFile.toPath(), maliciousFile.toPath());

        FileUploadResponse response = given()
                .multiPart("file", maliciousFile, "text/plain")
                .when()
                .post("/api/uploads/file")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(FileUploadResponse.class);

        assertThat(response.originalFileName()).doesNotContain("..");
        assertThat(response.originalFileName()).doesNotContain("/");
        assertThat(response.originalFileName()).doesNotContain("\\");

        // 저장 경로가 uploads 디렉토리 내부인지 확인
        List<UploadHistory> histories = uploadHistoryRepository.findAll();
        String storedPath = histories.get(0).getStoredFilename();
        assertThat(storedPath).contains(UPLOAD_DIR);

        maliciousFile.delete();
    }

    @Test
    void 파일_업로드_성공_후_차단_확장자_추가하면_새_업로드_차단() throws IOException {
        File pdfFile1 = createTempFile("document1.pdf", "content1");

        given()
                .multiPart("file", pdfFile1, "application/pdf")
                .when()
                .post("/api/uploads/file")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        assertThat(uploadHistoryRepository.findAll())
                .hasSize(1)
                .allMatch(h -> h.getUploadStatus() == UploadStatus.SUCCESS);

        // 커스텀 차단 확장자로 추가
        given()
                .contentType(ContentType.JSON)
                .body(new CustomExtensionRequest("pdf"))
                .post("/api/extensions/custom")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // 새로운 pdf 파일 업로드 차단
        File pdfFile2 = createTempFile("document2.pdf", "content2");

        given()
                .multiPart("file", pdfFile2, "application/pdf")
                .when()
                .post("/api/uploads/file")
                .then()
                .statusCode(ExtensionErrorCode.BLOCKED_EXTENSION.getHttpStatus().value());

        // 히스토리 확인
        List<UploadHistory> histories = uploadHistoryRepository.findAll();
        assertThat(histories).hasSize(2);
        assertThat(histories.get(0).getUploadStatus()).isEqualTo(UploadStatus.SUCCESS);
        assertThat(histories.get(1).getUploadStatus()).isEqualTo(UploadStatus.BLOCKED);

        pdfFile1.delete();
        pdfFile2.delete();
    }

    @Test
    void 업로드_플로우_테스트_성공과_차단_모두_기록() throws IOException {
        // 허용된 파일 업로드 성공
        File allowedFile = createTempFile("document.txt", "content1");
        given()
                .multiPart("file", allowedFile, "text/plain")
                .when()
                .post("/api/uploads/file")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // exe를 차단 상태로 설정
        given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(true))
                .when()
                .put("/api/extensions/fixed/exe");

        // 차단된 파일 업로드 시도
        File blockedFile = createTempFile("virus.exe", "malicious");
        given()
                .multiPart("file", blockedFile, "application/octet-stream")
                .when()
                .post("/api/uploads/file")
                .then()
                .statusCode(ExtensionErrorCode.BLOCKED_EXTENSION.getHttpStatus().value());

        // 다시 허용된 파일 업로드
        File allowedFile2 = createTempFile("image.jpg", "image data");
        given()
                .multiPart("file", allowedFile2, "image/jpeg")
                .when()
                .post("/api/uploads/file")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // 히스토리 검증
        List<UploadHistory> histories = uploadHistoryRepository.findAll();
        assertThat(histories).hasSize(3);

        long successCount = histories.stream()
                .filter(h -> h.getUploadStatus() == UploadStatus.SUCCESS)
                .count();
        long blockedCount = histories.stream()
                .filter(h -> h.getUploadStatus() == UploadStatus.BLOCKED)
                .count();

        assertThat(successCount).isEqualTo(2);
        assertThat(blockedCount).isEqualTo(1);

        allowedFile.delete();
        blockedFile.delete();
        allowedFile2.delete();
    }

    @Test
    void 확장자_없는_파일_업로드_시_빈_확장자로_처리() throws IOException {
        File tempFile = createTempFile("test.txt", "readme content");
        File noExtFile = new File(tempFile.getParent(), "README");
        Files.move(tempFile.toPath(), noExtFile.toPath());

        FileUploadResponse response = given()
                .multiPart("file", noExtFile, "text/plain")
                .when()
                .post("/api/uploads/file")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(FileUploadResponse.class);

        assertThat(response.fileExtension()).isEmpty();
        assertThat(response.originalFileName()).isEqualTo("README");

        noExtFile.delete();
    }

    @Test
    void 여러_파일_연속_업로드_히스토리_누적() throws IOException {
        File file1 = createTempFile("file1.txt", "content1");
        File file2 = createTempFile("file2.pdf", "content2");
        File file3 = createTempFile("file3.jpg", "content3");

        given().multiPart("file", file1, "text/plain")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());

        given().multiPart("file", file2, "application/pdf")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());

        given().multiPart("file", file3, "image/jpeg")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());

        List<UploadHistory> histories = uploadHistoryRepository.findAll();
        assertThat(histories).hasSize(3)
                .allMatch(h -> h.getUploadStatus() == UploadStatus.SUCCESS)
                .extracting(UploadHistory::getFileExtension)
                .containsExactlyInAnyOrder("txt", "pdf", "jpg");

        file1.delete();
        file2.delete();
        file3.delete();
    }

    // 테스트용 임시 파일 생성
    private File createTempFile(String filename, String content) throws IOException {
        String extension = "";
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = filename.substring(dotIndex);
        }

        File tempFile = File.createTempFile("test-", extension);
        Files.writeString(tempFile.toPath(), content);
        return tempFile;
    }
}
