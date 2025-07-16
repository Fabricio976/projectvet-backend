package com.project.TestsRest;

import com.project.model.dto.RegisterAnimalDTO;
import com.project.model.dto.RegisterUserDTO;
import com.project.model.dto.AuthenticationDTO;
import com.project.model.entitys.enums.RoleName;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AnimalIntegrationTest extends BaseIntegrationTest {

    @Test
    public void shouldRegisterAnimalSuccessfully() {
        RegisterAnimalDTO animalDTO = new RegisterAnimalDTO(
                "Buddy",3,"Dog",  "Labrador", clientCpf,  null
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .body(animalDTO)
                .when()
                .post("/animal/register")
                .then()
                .statusCode(200)
                .body("message", equalTo("Animal Registrado!"));
    }

    @Test
    public void shouldListOnlyOwnAnimalsForClient() {
        RegisterAnimalDTO animalDTO = new RegisterAnimalDTO(
                "Poodle", 2, "Max", clientCpf, "Dog", null
        );

        // Registra animal
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .body(animalDTO)
                .when()
                .post("/animal/register")
                .then()
                .statusCode(200);

        // Lista como cliente
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .get("/animal/searchAll?page=0&size=5")
                .then()
                .statusCode(200)
                .body("totalElements", greaterThanOrEqualTo(1))
                .body("content.name", hasItem("Max"));
    }

    @Test
    public void shouldListAllAnimalsForManager() {
        // 1. Registra animal para cliente existente
        RegisterAnimalDTO animal1 = new RegisterAnimalDTO(
                "Golden", 4, "Bella",  "Dog", clientCpf,null
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .body(animal1)
                .when()
                .post("/animal/register")
                .then()
                .statusCode(200);

        // 2. Registra novo cliente + animal
        String anotherEmail = "another_" + UUID.randomUUID() + "@example.com";
        String anotherCpf = generateUniqueCpf();

        RegisterUserDTO newClient = new RegisterUserDTO(
                "Another Client", anotherEmail, "password123", anotherCpf, RoleName.ROLE_CLIENT, "Rua Nova", "999888777"
        );

        // Cadastro
        given()
                .contentType(ContentType.JSON)
                .body(newClient)
                .when()
                .post("/register/client")
                .then()
                .statusCode(200);

        // Login
        AuthenticationDTO authDTO = new AuthenticationDTO(anotherEmail, "password123");
        String anotherToken = given()
                .contentType(ContentType.JSON)
                .body(authDTO)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        // Registra animal para novo cliente
        RegisterAnimalDTO animal2 = new RegisterAnimalDTO(
                "Siamese", 1, "Luna",  "Cat", anotherCpf,null
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + anotherToken)
                .body(animal2)
                .when()
                .post("/animal/register")
                .then()
                .statusCode(200);

        // 3. Lista como gerente (espera ver todos)
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .get("/animal/searchAll?page=0&size=10")
                .then()
                .statusCode(200)
                .body("totalElements", greaterThanOrEqualTo(2))
                .body("content.name", hasItems("Bella", "Luna"));
    }
}
