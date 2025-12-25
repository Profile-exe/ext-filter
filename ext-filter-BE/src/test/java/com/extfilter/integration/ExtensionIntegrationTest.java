package com.extfilter.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.extfilter.common.exception.CommonErrorCode;
import com.extfilter.common.exception.ErrorResponse;
import com.extfilter.domain.extension.dto.CustomExtensionRequest;
import com.extfilter.domain.extension.dto.CustomExtensionResponse;
import com.extfilter.domain.extension.dto.FixedExtensionResponse;
import com.extfilter.domain.extension.dto.ToggleBlockRequest;
import com.extfilter.domain.extension.exception.ExtensionErrorCode;
import com.extfilter.domain.extension.repository.CustomExtensionRepository;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.util.List;
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
class ExtensionIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CustomExtensionRepository customExtensionRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        customExtensionRepository.deleteAll();
    }

    @Test
    void 고정_확장자_조회_가능() {
        List<FixedExtensionResponse> response = given()
                .when()
                .get("/api/extensions/fixed")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        assertThat(response)
                .hasSize(7)
                .allMatch(ext -> ext.extensionName() != null);
    }

    @ParameterizedTest(name = "고정 확장자 {0} 차단 상태 {1}로 변경 성공")
    @CsvSource(
            value = {
                    "bat,true",
                    "cmd,true",
                    "com,true",
                    "cpl,true",
                    "exe,true",
                    "scr,true",
                    "js,true",
                    "bat,false",
                    "cmd,false",
                    "com,false",
                    "cpl,false",
                    "exe,false",
                    "scr,false",
                    "js,false"
            }
    )
    void 고정_확장자_차단_상태_변경_성공(String extensionName, boolean isBlocked) {

        FixedExtensionResponse response = given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(isBlocked))
                .when()
                .put("/api/extensions/fixed/" + extensionName)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(FixedExtensionResponse.class);

        assertThat(response.extensionName()).isEqualTo(extensionName);
        assertThat(response.isBlocked()).isEqualTo(isBlocked);
    }

    @Test
    void 고정_확장자_차단_상태_변경_실패_존재하지_않는_확장자() {
        ErrorResponse error = given()
                .contentType(ContentType.JSON)
                .body(new ToggleBlockRequest(true))
                .when()
                .put("/api/extensions/fixed/invalid")
                .then()
                .statusCode(ExtensionErrorCode.FIXED_EXTENSION_NOT_FOUND.getHttpStatus().value())
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.code()).isEqualTo(ExtensionErrorCode.FIXED_EXTENSION_NOT_FOUND.getCode());
        assertThat(error.message()).isEqualTo(ExtensionErrorCode.FIXED_EXTENSION_NOT_FOUND.getMessage());
    }

    @Test
    void 커스텀_확장자_빈_조회_가능() {
        List<CustomExtensionResponse> response = given()
                .when()
                .get("/api/extensions/custom")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        assertThat(response).isEmpty();
    }

    @ParameterizedTest(name = "커스텀 확장자 생성 요청: {0}")
    @CsvSource(
            value = {
                    "pdf",
                    "docx",
                    "xlsx",
                    "pptx",
                    "txt"
            }
    )
    void 커스텀_확장자_추가_성공(String extensionName) {
        CustomExtensionResponse response = given()
                .contentType(ContentType.JSON)
                .body(new CustomExtensionRequest(extensionName))
                .post("/api/extensions/custom")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(CustomExtensionResponse.class);

        assertThat(response.extensionName()).isEqualTo(extensionName);
        assertThat(response.id()).isNotNull();
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void 커스텀_확장자_최대_개수_초과_방지() {
        // 최대 개수(200)까지 추가
        for (int i = 1; i <= 200; i++) {
            String extensionName = "ext" + i;
            given()
                    .contentType(ContentType.JSON)
                    .body(new CustomExtensionRequest(extensionName))
                    .post("/api/extensions/custom")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());
        }

        // 201번째 추가 시도
        ErrorResponse error = given()
                .contentType(ContentType.JSON)
                .body(new CustomExtensionRequest("ext201"))
                .when()
                .post("/api/extensions/custom")
                .then()
                .statusCode(ExtensionErrorCode.EXTENSION_LIMIT_EXCEEDED.getHttpStatus().value())
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.code()).isEqualTo(ExtensionErrorCode.EXTENSION_LIMIT_EXCEEDED.getCode());
        assertThat(error.message()).isEqualTo(ExtensionErrorCode.EXTENSION_LIMIT_EXCEEDED.getMessage());
    }

    @Test
    void 커스텀_확장자_중복_추가_방지() {
        String extensionName = "pdf";

        // 먼저 추가
        given()
                .contentType(ContentType.JSON)
                .body(new CustomExtensionRequest(extensionName))
                .post("/api/extensions/custom");

        // 중복 추가 시도
        ErrorResponse error = given()
                .contentType(ContentType.JSON)
                .body(new CustomExtensionRequest(extensionName))
                .when()
                .post("/api/extensions/custom")
                .then()
                .statusCode(ExtensionErrorCode.DUPLICATE_EXTENSION.getHttpStatus().value())
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.code()).isEqualTo(ExtensionErrorCode.DUPLICATE_EXTENSION.getCode());
        assertThat(error.message()).isEqualTo(ExtensionErrorCode.DUPLICATE_EXTENSION.getMessage());
    }

    @Test
    void 커스텀_확장자_고정_확장자_중복_추가_방지() {
        String fixedExtensionName = "exe";

        // 중복 추가 시도
        ErrorResponse error = given()
                .contentType(ContentType.JSON)
                .body(new CustomExtensionRequest(fixedExtensionName))
                .when()
                .post("/api/extensions/custom")
                .then()
                .statusCode(ExtensionErrorCode.DUPLICATE_EXTENSION.getHttpStatus().value())
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.code()).isEqualTo(ExtensionErrorCode.DUPLICATE_EXTENSION.getCode());
        assertThat(error.message()).isEqualTo(ExtensionErrorCode.DUPLICATE_EXTENSION.getMessage());
    }

    @ParameterizedTest(name = "커스텀 확장자 추가 실패 - 잘못된 형식: {0}")
    @CsvSource(
            value = {
                    "한글 확장자",
                    "'   '",
                    "!@#$%",
                    "longextensionnameexceedinglimit"
            }
    )
    void 커스텀_확장자_추가_실패_잘못된_형식(String invalidExtensionName) {
        given()
                .contentType(ContentType.JSON)
                .body(new CustomExtensionRequest(invalidExtensionName))
                .post("/api/extensions/custom")
                .then()
                .statusCode(CommonErrorCode.INVALID_INPUT_VALUE.getHttpStatus().value());
    }

    @Test
    void 커스텀_확장자_삭제_성공() {
        CustomExtensionResponse created = given()
                .contentType(ContentType.JSON)
                .body(new CustomExtensionRequest("pdf"))
                .post("/api/extensions/custom")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(CustomExtensionResponse.class);

        given()
                .when()
                .delete("/api/extensions/custom/" + created.id())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 커스텀_확장자_삭제_실패_존재하지_않는_확장자() {
        String nonExistentId = "9999";

        ErrorResponse error = given()
                .when()
                .delete("/api/extensions/custom/" + nonExistentId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.code()).isEqualTo(ExtensionErrorCode.CUSTOM_EXTENSION_NOT_FOUND.getCode());
        assertThat(error.message()).isEqualTo(ExtensionErrorCode.CUSTOM_EXTENSION_NOT_FOUND.getMessage());
    }

    @Test
    void 커스텀_확장자_추가_조회_삭제_흐름_테스트() {
        String extensionName = "pdf";

        // 1. 추가
        CustomExtensionResponse created = given()
                .contentType(ContentType.JSON)
                .body(new CustomExtensionRequest(extensionName))
                .when()
                .post("/api/extensions/custom")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(CustomExtensionResponse.class);

        assertThat(created.extensionName()).isEqualTo(extensionName);
        assertThat(created.id()).isNotNull();

        // 2. 조회 확인
        List<CustomExtensionResponse> extensions = given()
                .when()
                .get("/api/extensions/custom")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        assertThat(extensions).hasSize(1);
        assertThat(extensions.get(0).id()).isEqualTo(created.id());
        assertThat(extensions.get(0).extensionName()).isEqualTo(extensionName);

        // 3. 삭제
        given()
                .when()
                .delete("/api/extensions/custom/" + created.id())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // 4. 삭제 확인
        List<CustomExtensionResponse> afterDelete = given()
                .when()
                .get("/api/extensions/custom")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(new TypeRef<>() {
                });

        assertThat(afterDelete).isEmpty();
    }
}
