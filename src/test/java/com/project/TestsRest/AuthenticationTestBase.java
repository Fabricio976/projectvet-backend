package com.project.TestsRest;

import static org.hamcrest.Matchers.notNullValue;
import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import com.project.model.dto.AuthenticationDTO;
import com.project.model.dto.RegisterUserDTO;
import com.project.model.entitys.enums.Role;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthenticationTestBase {

    @LocalServerPort
    private int port;

    @BeforeAll
    void setupOnce() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "/projectvet";

        RegisterUserDTO funcionario = new RegisterUserDTO(
                "Pedro Luiz",
                "test_pedroluiz@example.com",
                "password123",
                "980172398",
                Role.ROLE_MANAGER,
                "123 Casa Grande",
                "1234567890");

        given()
                .contentType(ContentType.JSON)
                .body(funcionario)
                .when()
                .post("/register/funcionario")
                .then()
                .statusCode(HttpStatus.OK.value());
    }




    private String generateUniqueEmail(String base) {
        return base + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    private void registerUser(RegisterUserDTO user, String endpoint) {
        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(endpoint)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testRegisterClientSuccess() {
        RegisterUserDTO client = new RegisterUserDTO(
                "Fabricio Felizardo",
                generateUniqueEmail("test_fabricio"),
                "password123",
                "12345678901",
                Role.ROLE_CLIENT,
                "123 Casa Grande",
                "1234567890");

        registerUser(client, "/register/client");
    }

    @Test
    public void testRegisterManagerSuccess() {
        RegisterUserDTO manager = new RegisterUserDTO(
                "Joao Maria",
                generateUniqueEmail("test_joaomaria"),
                "password123",
                "32145678901",
                Role.ROLE_MANAGER,
                "123 Main St",
                "4324567890");

        registerUser(manager, "/register/funcionario");
    }

    @Test
    public void testRegisterClientEmailAlreadyExists() {
        String email = generateUniqueEmail("test_fabricio");
        RegisterUserDTO client = new RegisterUserDTO(
                "Fabricio Felizardo",
                email,
                "password123",
                "12345678901",
                Role.ROLE_CLIENT,
                "123 Casa Grandee",
                "1234567890");

        registerUser(client, "/register/client");

        given()
                .contentType(ContentType.JSON)
                .body(client)
                .when()
                .post("/register/client")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void testLoginClientSuccess() {
        String email = generateUniqueEmail("test_fabricio");
        RegisterUserDTO client = new RegisterUserDTO(
                "Fabricio Felizardo",
                email,
                "password123",
                "12345678901",
                Role.ROLE_CLIENT,
                "123 Casa Grande",
                "1234567890");

        registerUser(client, "/register/client");

        AuthenticationDTO loginData = new AuthenticationDTO(email, "password123");

        given()
                .contentType(ContentType.JSON)
                .body(loginData)
                .when()
                .post("/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("token", notNullValue())
                .body("userId", notNullValue());
    }

    @Test
    void testLoginManagerSuccess() {
        String email = generateUniqueEmail("test_joaomaria");
        RegisterUserDTO manager = new RegisterUserDTO(
                "Joao Maria",
                email,
                "password123",
                "32145678901",
                Role.ROLE_MANAGER,
                "123 Main St",
                "4324567890");

        registerUser(manager, "/register/funcionario");

        AuthenticationDTO loginData = new AuthenticationDTO(email, "password123");

        given()
                .contentType(ContentType.JSON)
                .body(loginData)
                .when()
                .post("/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("token", notNullValue())
                .body("userId", notNullValue());
    }

    @Test
    public void testLoginInvalidCredentials() {
        AuthenticationDTO loginData = new AuthenticationDTO("invalid@example.com", "wrongpassword");

        given()
                .contentType(ContentType.JSON)
                .body(loginData)
                .when()
                .post("/login")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
