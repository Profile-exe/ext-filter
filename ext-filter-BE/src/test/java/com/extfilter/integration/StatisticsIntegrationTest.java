package com.extfilter.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.extfilter.domain.extension.dto.ToggleBlockRequest;
import com.extfilter.domain.extension.repository.CustomExtensionRepository;
import com.extfilter.domain.upload.dto.DailyUploadDto;
import com.extfilter.domain.upload.dto.ExtensionCountDto;
import com.extfilter.domain.upload.dto.StatisticsResponse;
import com.extfilter.domain.upload.repository.UploadHistoryRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
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
class StatisticsIntegrationTest {

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
    void 통계_개요_조회_성공_데이터_없음() {
        // when - 데이터가 없을 때
        StatisticsResponse response = given()
                .when()
                .get("/api/statistics/overview")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(StatisticsResponse.class);

        // then
        assertThat(response.totalUploads()).isZero();
        assertThat(response.successCount()).isZero();
        assertThat(response.blockedCount()).isZero();
        assertThat(response.blockingRate()).isZero();
        assertThat(response.topBlockedExtensions()).isEmpty();
        assertThat(response.uploadTrend()).isEmpty();
    }

    @Test
    void 통계_개요_조회_성공_성공만_있는_경우() throws IOException {
        // given - 성공한 업로드만 3건
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
        StatisticsResponse response = given()
                .when()
                .get("/api/statistics/overview")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(StatisticsResponse.class);

        // then
        assertThat(response.totalUploads()).isEqualTo(3L);
        assertThat(response.successCount()).isEqualTo(3L);
        assertThat(response.blockedCount()).isZero();
        assertThat(response.blockingRate()).isZero();

        file1.delete();
        file2.delete();
        file3.delete();
    }

    @Test
    void 통계_개요_조회_성공_성공과_차단_혼합() throws IOException {
        // given - 성공 2건, 차단 3건
        File allowedFile1 = createTempFile("allowed1.txt", "content1");
        File allowedFile2 = createTempFile("allowed2.pdf", "content2");
        given().multiPart("file", "allowed1.txt", Files.readAllBytes(allowedFile1.toPath()), "text/plain")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());
        given().multiPart("file", "allowed2.pdf", Files.readAllBytes(allowedFile2.toPath()), "application/pdf")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());

        // exe, bat, cmd를 차단 상태로 설정
        given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(true))
                .when()
                .put("/api/extensions/fixed/exe");
        given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(true))
                .when()
                .put("/api/extensions/fixed/bat");
        given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(true))
                .when()
                .put("/api/extensions/fixed/cmd");

        File blockedFile1 = createTempFile("blocked1.exe", "malicious1");
        File blockedFile2 = createTempFile("blocked2.bat", "malicious2");
        File blockedFile3 = createTempFile("blocked3.cmd", "malicious3");
        given().multiPart("file", "blocked1.exe", Files.readAllBytes(blockedFile1.toPath()), "application/octet-stream")
                .post("/api/uploads/file");
        given().multiPart("file", "blocked2.bat", Files.readAllBytes(blockedFile2.toPath()), "application/octet-stream")
                .post("/api/uploads/file");
        given().multiPart("file", "blocked3.cmd", Files.readAllBytes(blockedFile3.toPath()), "application/octet-stream")
                .post("/api/uploads/file");

        // when
        StatisticsResponse response = given()
                .when()
                .get("/api/statistics/overview")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(StatisticsResponse.class);

        // then
        assertThat(response.totalUploads()).isEqualTo(5L);
        assertThat(response.successCount()).isEqualTo(2L);
        assertThat(response.blockedCount()).isEqualTo(3L);

        // 차단율 계산: 3/5 * 100 = 60.0
        double blockingRate = response.blockingRate();
        assertThat(blockingRate).isEqualTo(60.0);

        allowedFile1.delete();
        allowedFile2.delete();
        blockedFile1.delete();
        blockedFile2.delete();
        blockedFile3.delete();
    }

    @Test
    void 통계_개요_조회_차단율_계산_확인() throws IOException {
        // given - 전체 10건 중 7건 차단
        for (int i = 1; i <= 3; i++) {
            File file = createTempFile("success" + i + ".txt", "content" + i);
            String filename = "success" + i + ".txt";
            given().multiPart("file", filename, Files.readAllBytes(file.toPath()), "text/plain")
                    .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());
            file.delete();
        }

        given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(true))
                .when()
                .put("/api/extensions/fixed/exe");

        for (int i = 1; i <= 7; i++) {
            File file = createTempFile("blocked" + i + ".exe", "malicious" + i);
            String filename = "blocked" + i + ".exe";
            given().multiPart("file", filename, Files.readAllBytes(file.toPath()), "application/octet-stream")
                    .post("/api/uploads/file");
            file.delete();
        }

        // when
        StatisticsResponse response = given()
                .when()
                .get("/api/statistics/overview")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(StatisticsResponse.class);

        // then
        assertThat(response.totalUploads()).isEqualTo(10L);
        assertThat(response.successCount()).isEqualTo(3L);
        assertThat(response.blockedCount()).isEqualTo(7L);

        // 차단율: 7/10 * 100 = 70.0
        double blockingRate = response.blockingRate();
        assertThat(blockingRate).isEqualTo(70.0);
    }

    @Test
    void 통계_개요_조회_TOP5_차단_확장자_확인() throws IOException {
        // given - 여러 확장자로 차단
        given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(true))
                .when()
                .put("/api/extensions/fixed/exe");
        given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(true))
                .when()
                .put("/api/extensions/fixed/bat");
        given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(true))
                .when()
                .put("/api/extensions/fixed/cmd");

        // exe 5번, bat 3번, cmd 1번 차단
        for (int i = 1; i <= 5; i++) {
            File file = createTempFile("blocked" + i + ".exe", "exe" + i);
            String filename = "blocked" + i + ".exe";
            given().multiPart("file", filename, Files.readAllBytes(file.toPath()), "application/octet-stream")
                    .post("/api/uploads/file");
            file.delete();
        }
        for (int i = 1; i <= 3; i++) {
            File file = createTempFile("blocked" + i + ".bat", "bat" + i);
            String filename = "blocked" + i + ".bat";
            given().multiPart("file", filename, Files.readAllBytes(file.toPath()), "application/octet-stream")
                    .post("/api/uploads/file");
            file.delete();
        }
        File cmdFile = createTempFile("blocked.cmd", "cmd");
        given().multiPart("file", "blocked.cmd", Files.readAllBytes(cmdFile.toPath()), "application/octet-stream")
                .post("/api/uploads/file");
        cmdFile.delete();

        // when
        StatisticsResponse response = given()
                .when()
                .get("/api/statistics/overview")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(StatisticsResponse.class);

        // then
        List<ExtensionCountDto> topBlockedExtensions = response.topBlockedExtensions();
        assertThat(topBlockedExtensions).hasSize(3);

        // 첫 번째는 exe (5번)
        assertThat(topBlockedExtensions.get(0).fileExtension()).isEqualTo("exe");
        assertThat(topBlockedExtensions.get(0).count()).isEqualTo(5L);

        // 두 번째는 bat (3번)
        assertThat(topBlockedExtensions.get(1).fileExtension()).isEqualTo("bat");
        assertThat(topBlockedExtensions.get(1).count()).isEqualTo(3L);

        // 세 번째는 cmd (1번)
        assertThat(topBlockedExtensions.get(2).fileExtension()).isEqualTo("cmd");
        assertThat(topBlockedExtensions.get(2).count()).isEqualTo(1L);
    }

    @Test
    void 통계_개요_조회_TOP5_제한_확인() throws IOException {
        // given - 6개 이상의 서로 다른 확장자 차단
        String[] extensions = {"exe", "bat", "cmd", "com", "cpl", "scr", "js"};

        for (String ext : extensions) {
            given()
                    .contentType(ContentType.JSON)
                    .body(new ToggleBlockRequest(true))
                    .when()
                    .put("/api/extensions/fixed/" + ext);

            File file = createTempFile("blocked." + ext, "content");
            String filename = "blocked." + ext;
            given().multiPart("file", filename, Files.readAllBytes(file.toPath()), "application/octet-stream")
                    .post("/api/uploads/file");
            file.delete();
        }

        // when
        StatisticsResponse response = given()
                .when()
                .get("/api/statistics/overview")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(StatisticsResponse.class);

        // then - TOP 5만 반환되어야 함
        List<ExtensionCountDto> topBlockedExtensions = response.topBlockedExtensions();
        assertThat(topBlockedExtensions).hasSizeLessThanOrEqualTo(5);
    }

    @Test
    void 통계_개요_조회_일별_업로드_추이_확인() throws IOException {
        // given - 파일 업로드
        File file1 = createTempFile("test1.txt", "content1");
        File file2 = createTempFile("test2.pdf", "content2");
        given().multiPart("file", "test1.txt", Files.readAllBytes(file1.toPath()), "text/plain")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());
        given().multiPart("file", "test2.pdf", Files.readAllBytes(file2.toPath()), "application/pdf")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());

        given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(true))
                .when()
                .put("/api/extensions/fixed/exe");

        File file3 = createTempFile("blocked.exe", "malicious");
        given().multiPart("file", "blocked.exe", Files.readAllBytes(file3.toPath()), "application/octet-stream")
                .post("/api/uploads/file");

        // when
        StatisticsResponse response = given()
                .when()
                .get("/api/statistics/overview")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(StatisticsResponse.class);

        // then
        List<DailyUploadDto> uploadTrend = response.uploadTrend();
        assertThat(uploadTrend).isNotEmpty();

        // 오늘 날짜의 통계가 포함되어야 함
        DailyUploadDto today = uploadTrend.get(0);
        assertThat(today.totalCount()).isEqualTo(3L);
        assertThat(today.successCount()).isEqualTo(2L);
        assertThat(today.blockedCount()).isEqualTo(1L);

        file1.delete();
        file2.delete();
        file3.delete();
    }

    @Test
    void 통계_개요_조회_전체_흐름_테스트() throws IOException {
        // given - 다양한 시나리오의 업로드
        // 1. 성공 업로드 3건
        File success1 = createTempFile("doc1.txt", "content1");
        File success2 = createTempFile("doc2.pdf", "content2");
        File success3 = createTempFile("img1.jpg", "content3");
        given().multiPart("file", "doc1.txt", Files.readAllBytes(success1.toPath()), "text/plain")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());
        given().multiPart("file", "doc2.pdf", Files.readAllBytes(success2.toPath()), "application/pdf")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());
        given().multiPart("file", "img1.jpg", Files.readAllBytes(success3.toPath()), "image/jpeg")
                .post("/api/uploads/file").then().statusCode(HttpStatus.CREATED.value());

        // 2. 차단 확장자 설정
        given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(true))
                .when()
                .put("/api/extensions/fixed/exe");
        given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(true))
                .when()
                .put("/api/extensions/fixed/bat");

        // 3. 차단 업로드 5건 (exe 3건, bat 2건)
        for (int i = 1; i <= 3; i++) {
            File file = createTempFile("malware" + i + ".exe", "exe" + i);
            String filename = "malware" + i + ".exe";
            given().multiPart("file", filename, Files.readAllBytes(file.toPath()), "application/octet-stream")
                    .post("/api/uploads/file");
            file.delete();
        }
        for (int i = 1; i <= 2; i++) {
            File file = createTempFile("virus" + i + ".bat", "bat" + i);
            String filename = "virus" + i + ".bat";
            given().multiPart("file", filename, Files.readAllBytes(file.toPath()), "application/octet-stream")
                    .post("/api/uploads/file");
            file.delete();
        }

        // when
        StatisticsResponse response = given()
                .when()
                .get("/api/statistics/overview")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(StatisticsResponse.class);

        // then - 전체 통계
        assertThat(response.totalUploads()).isEqualTo(8L);
        assertThat(response.successCount()).isEqualTo(3L);
        assertThat(response.blockedCount()).isEqualTo(5L);

        // 차단율: 5/8 * 100 = 62.5
        double blockingRate = response.blockingRate();
        assertThat(blockingRate).isEqualTo(62.5);

        // TOP 차단 확장자
        List<ExtensionCountDto> topBlockedExtensions = response.topBlockedExtensions();
        assertThat(topBlockedExtensions).hasSize(2);
        assertThat(topBlockedExtensions.get(0).fileExtension()).isEqualTo("exe");
        assertThat(topBlockedExtensions.get(0).count()).isEqualTo(3L);
        assertThat(topBlockedExtensions.get(1).fileExtension()).isEqualTo("bat");
        assertThat(topBlockedExtensions.get(1).count()).isEqualTo(2L);

        // 일별 추이
        List<DailyUploadDto> uploadTrend = response.uploadTrend();
        assertThat(uploadTrend).isNotEmpty();

        success1.delete();
        success2.delete();
        success3.delete();
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
