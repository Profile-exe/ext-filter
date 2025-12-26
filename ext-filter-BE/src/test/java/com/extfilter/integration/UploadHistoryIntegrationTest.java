package com.extfilter.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.extfilter.common.PageTemplate;
import com.extfilter.domain.extension.dto.ToggleBlockRequest;
import com.extfilter.domain.extension.repository.CustomExtensionRepository;
import com.extfilter.domain.upload.dto.UploadHistoryResponse;
import com.extfilter.domain.upload.entity.UploadStatus;
import com.extfilter.domain.upload.repository.UploadHistoryRepository;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UploadHistoryIntegrationTest {

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
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (Files.exists(uploadPath)) {
            Files.walk(uploadPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    void 업로드_히스토리_전체_조회_성공() throws IOException {
        // given - 여러 파일 업로드
        File file1 = createTempFile("test1.txt", "content1");
        File file2 = createTempFile("test2.pdf", "content2");
        File file3 = createTempFile("test3.jpg", "content3");

        given().multiPart("file", "test1.txt", Files.readAllBytes(file1.toPath()), "text/plain")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());
        given().multiPart("file", "test2.pdf", Files.readAllBytes(file2.toPath()), "application/pdf")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());
        given().multiPart("file", "test3.jpg", Files.readAllBytes(file3.toPath()), "image/jpeg")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());

        // when
        PageTemplate<UploadHistoryResponse> response = given()
                .when()
                .get("/api/uploads/history")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(response.getContent()).hasSize(3);
        assertThat(response.getTotalElements()).isEqualTo(3);

        file1.delete();
        file2.delete();
        file3.delete();
    }

    @Test
    void 업로드_히스토리_상태별_필터링_성공() throws IOException {
        // given - SUCCESS와 BLOCKED 모두 생성
        File allowedFile = createTempFile("allowed.txt", "content");
        given().multiPart("file", "allowed.txt", Files.readAllBytes(allowedFile.toPath()), "text/plain")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());

        // exe를 차단 상태로 설정
        given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(true))
                .when()
                .put("/api/extensions/fixed/exe");

        File blockedFile = createTempFile("blocked.exe", "malicious");
        given().multiPart("file", "blocked.exe", Files.readAllBytes(blockedFile.toPath()), "application/octet-stream")
                .post("/api/uploads/file");

        // when - BLOCKED만 필터링
        PageTemplate<UploadHistoryResponse> response = given()
                .queryParam("status", "BLOCKED")
                .when()
                .get("/api/uploads/history")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).uploadStatus()).isEqualTo(UploadStatus.BLOCKED);
        assertThat(response.getContent().get(0).fileExtension()).isEqualTo("exe");

        allowedFile.delete();
        blockedFile.delete();
    }

    @Test
    void 업로드_히스토리_확장자별_필터링_성공() throws IOException {
        // given - 여러 확장자 파일 업로드
        File txtFile = createTempFile("test1.txt", "content1");
        File pdfFile = createTempFile("test2.pdf", "content2");
        File jpgFile = createTempFile("test3.jpg", "content3");

        given().multiPart("file", "test1.txt", Files.readAllBytes(txtFile.toPath()), "text/plain")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());
        given().multiPart("file", "test2.pdf", Files.readAllBytes(pdfFile.toPath()), "application/pdf")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());
        given().multiPart("file", "test3.jpg", Files.readAllBytes(jpgFile.toPath()), "image/jpeg")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());

        // when - pdf만 필터링
        PageTemplate<UploadHistoryResponse> response = given()
                .queryParam("extension", "pdf")
                .when()
                .get("/api/uploads/history")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).fileExtension()).isEqualTo("pdf");

        txtFile.delete();
        pdfFile.delete();
        jpgFile.delete();
    }

    @Test
    void 업로드_히스토리_상태와_확장자_복합_필터링_성공() throws IOException {
        // given
        // exe를 차단 상태로 설정
        given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(true))
                .when()
                .put("/api/extensions/fixed/exe");

        File exeFile = createTempFile("blocked.exe", "malicious");
        given().multiPart("file", "blocked.exe", Files.readAllBytes(exeFile.toPath()), "application/octet-stream")
                .post("/api/uploads/file");

        File txtFile = createTempFile("allowed.txt", "content");
        given().multiPart("file", "allowed.txt", Files.readAllBytes(txtFile.toPath()), "text/plain")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());

        // when - BLOCKED + exe 필터링
        PageTemplate<UploadHistoryResponse> response = given()
                .queryParam("status", "BLOCKED")
                .queryParam("extension", "exe")
                .when()
                .get("/api/uploads/history")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).uploadStatus()).isEqualTo(UploadStatus.BLOCKED);
        assertThat(response.getContent().get(0).fileExtension()).isEqualTo("exe");

        exeFile.delete();
        txtFile.delete();
    }

    @Test
    void 업로드_히스토리_페이지네이션_성공() throws IOException {
        // given - 25개 파일 업로드 (기본 페이지 크기 20개)
        for (int i = 1; i <= 25; i++) {
            File file = createTempFile("test" + i + ".txt", "content" + i);
            String filename = "test" + i + ".txt";
            given().multiPart("file", filename, Files.readAllBytes(file.toPath()), "text/plain")
                    .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());
            file.delete();
        }

        // when - 첫 번째 페이지 조회
        PageTemplate<UploadHistoryResponse> page0 = given()
                .queryParam("page", 0)
                .queryParam("size", 20)
                .when()
                .get("/api/uploads/history")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(page0.getContent()).hasSize(20);
        assertThat(page0.getTotalElements()).isEqualTo(25);
        assertThat(page0.getTotalPages()).isEqualTo(2);

        // when - 두 번째 페이지 조회
        PageTemplate<UploadHistoryResponse> page1 = given()
                .queryParam("page", 1)
                .queryParam("size", 20)
                .when()
                .get("/api/uploads/history")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(page1.getContent()).hasSize(5);
    }

    @Test
    void 업로드_히스토리_최신순_정렬_확인() throws IOException {
        // given
        File file1 = createTempFile("first.txt", "content1");
        given().multiPart("file", "first.txt", Files.readAllBytes(file1.toPath()), "text/plain")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());

        File file2 = createTempFile("second.txt", "content2");
        given().multiPart("file", "second.txt", Files.readAllBytes(file2.toPath()), "text/plain")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());

        // when
        PageTemplate<UploadHistoryResponse> response = given()
                .when()
                .get("/api/uploads/history")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        // then - 최신순이므로 second.txt가 먼저 나와야 함
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().get(0).originalFilename()).isEqualTo("second.txt");
        assertThat(response.getContent().get(1).originalFilename()).isEqualTo("first.txt");

        file1.delete();
        file2.delete();
    }

    @Test
    void 업로드_히스토리_빈_결과_조회() {
        // when - 데이터가 없을 때
        PageTemplate<UploadHistoryResponse> response = given()
                .when()
                .get("/api/uploads/history")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isZero();
    }

    @Test
    void 업로드_히스토리_SUCCESS_필터링_성공() throws IOException {
        // given
        File allowedFile1 = createTempFile("file1.txt", "content1");
        File allowedFile2 = createTempFile("file2.pdf", "content2");
        given().multiPart("file", "file1.txt", Files.readAllBytes(allowedFile1.toPath()), "text/plain")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());
        given().multiPart("file", "file2.pdf", Files.readAllBytes(allowedFile2.toPath()), "application/pdf")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());

        // when - SUCCESS만 필터링
        PageTemplate<UploadHistoryResponse> response = given()
                .queryParam("status", "SUCCESS")
                .when()
                .get("/api/uploads/history")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent()).allMatch(item -> item.uploadStatus() == UploadStatus.SUCCESS);

        allowedFile1.delete();
        allowedFile2.delete();
    }

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
