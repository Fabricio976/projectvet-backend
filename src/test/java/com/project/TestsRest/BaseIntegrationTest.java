package com.project.TestsRest;

import com.project.model.dto.AuthenticationDTO;
import com.project.model.dto.RegisterUserDTO;
import com.project.model.entitys.enums.RoleName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.Random;
import java.util.UUID;

import static io.restassured.RestAssured.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    protected String clientToken;
    protected String managerToken;
    protected String clientCpf;
    protected String managerCpf;

    @BeforeAll
    public void baseSetup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "/projectvet";

        registerClient();
        registerManager();
    }

    protected void registerClient() {
        String clientEmail = "client_" + UUID.randomUUID() + "@example.com";
        clientCpf = generateUniqueCpf();
        RegisterUserDTO clientDTO = new RegisterUserDTO(
                "Client User", clientEmail, "password123", clientCpf, RoleName.ROLE_CLIENT, "Rua", "123456789"
        );

        given()
                .contentType(ContentType.JSON)
                .body(clientDTO)
                .when()
                .post("/register/client")
                .then()
                .statusCode(200);

        AuthenticationDTO auth = new AuthenticationDTO(clientEmail, "password123");
        clientToken = given()
                .contentType(ContentType.JSON)
                .body(auth)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    protected void registerManager() {
        String managerEmail = "manager_" + UUID.randomUUID() + "@example.com";
        managerCpf = generateUniqueCpf();
        RegisterUserDTO managerDTO = new RegisterUserDTO(
                "Manager User", managerEmail, "password123", managerCpf, RoleName.ROLE_MANAGER, "Rua", "987654321"
        );

        given()
                .contentType(ContentType.JSON)
                .body(managerDTO)
                .when()
                .post("/register/manager")
                .then()
                .statusCode(200);

        AuthenticationDTO auth = new AuthenticationDTO(managerEmail, "password123");
        managerToken = given()
                .contentType(ContentType.JSON)
                .body(auth)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }


    protected String generateUniqueCpf() {

        Random random = new Random();
        int[] digits = new int[11];

        // Gera os 9 primeiros digito aleatórios
        for (int i = 0; i < 9; i++) {
            digits[i] = random.nextInt(10);
        }

        // Calcula o primeiro digito
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += digits[i] * (10 - i);
        }
        int firstVerifier = 11 - (sum % 11);
        if (firstVerifier >= 10) {
            firstVerifier = 0;
        }
        digits[9] = firstVerifier;

        // Calcula o segundo digito
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += digits[i] * (11 - i);
        }
        int secondVerifier = 11 - (sum % 11);
        if (secondVerifier >= 10) {
            secondVerifier = 0;
        }
        digits[10] = secondVerifier;

        // Formata o CPF como 000.000.000-00
        return String.format("%d%d%d.%d%d%d.%d%d%d-%d%d",
                digits[0], digits[1], digits[2],
                digits[3], digits[4], digits[5],
                digits[6], digits[7], digits[8],
                digits[9], digits[10]);
    }

}
