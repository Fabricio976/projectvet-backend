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
    public void registerAnimal_Success() {
        RegisterAnimalDTO animalDTO = new RegisterAnimalDTO(
                "Buddy", 3, "Dog", "Labrador", clientCpf, null
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
    public void listPorClient() {
        RegisterAnimalDTO animalDTO = new RegisterAnimalDTO(
                "Toto", 2, "Poodle", "Dog", clientCpf, null
        );

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
                .body("page.totalElements", greaterThanOrEqualTo(1))
                .body("content.name", hasItem("Toto"));
    }

    @Test
    public void listAllAnimals_ParaManager() {
        // Registra animal para cliente existente
        RegisterAnimalDTO animal1 = new RegisterAnimalDTO(
                "Juarez", 4, "Golden", "Dog", clientCpf, null
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .body(animal1)
                .when()
                .post("/animal/register")
                .then()
                .statusCode(200);

        // Registra novo cliente + animal
        String anotherEmail = "another_" + UUID.randomUUID() + "@example.com";
        String anotherCpf = generateUniqueCpf();

        RegisterUserDTO newClient = new RegisterUserDTO(
                "Client 2", anotherEmail, "password123", anotherCpf, RoleName.ROLE_CLIENT, "Rua Nova", "999888777"
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
                "Luna", 1, "Siamese", "Cat", anotherCpf, null
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + anotherToken)
                .body(animal2)
                .when()
                .post("/animal/register")
                .then()
                .statusCode(200);

        // Lista como adm (espera ver todos)
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .get("/animal/searchAll?page=0&size=10")
                .then()
                .statusCode(200)
                .body("page.totalElements", greaterThanOrEqualTo(2))
                .body("content.name", hasItems("Juarez", "Luna"));
    }

    @Test
    public void listAnimal_ByCpf() {
        RegisterAnimalDTO animalDTO = new RegisterAnimalDTO(
                "Jurubeba", 2, "Poodle", "Dog", clientCpf, null
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .body(animalDTO)
                .when()
                .post("/animal/register")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .get("/animal/searchByUserCpf/" + clientCpf)
                .then()
                .statusCode(200)
                .body("name", hasItem("Jurubeba"));
    }


}
