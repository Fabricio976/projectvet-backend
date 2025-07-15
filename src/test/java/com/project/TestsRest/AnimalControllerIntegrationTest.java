package com.project.TestsRest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AnimalControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private String managerToken;
    private String clientToken;
    private String managerUserId;
    private String clientUserId;
    private String animalId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/projectvet/animal";

        UsuarioInfo manager = TestUtils.criarUsuarioValido("MANAGER");
        UsuarioInfo client = TestUtils.criarUsuarioValido("CLIENT");

        managerToken = TestUtils.obterToken(manager.email(), manager.password());
        clientToken = TestUtils.obterToken(client.email(), client.password());

        managerUserId = manager.id();
        clientUserId = client.id();

        animalId = TestUtils.criarAnimalComUsuario(managerToken, manager.cpf());
    }


    private RequestSpecification authAsManager() {
        return given()
                .header("Authorization", "Bearer " + managerToken)
                .contentType(ContentType.JSON);
    }

    private RequestSpecification authAsClient() {
        return given()
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON);
    }

    private Map<String, Object> animalRequest(String cpf) {
        return Map.of(
                "name", "Rex",
                "age", 5,
                "race", "Labrador",
                "specie", "Dog",
                "photoUrl", "",
                "responsible", cpf
        );
    }

    @Test
    void searchAllAnimals_AsManager_ShouldReturnAnimals() {
        authAsManager()
                .queryParam("page", 0)
                .queryParam("size", 5)
                .when()
                .get("/searchAll")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0))
                .body("content[0].id", equalTo(animalId));
    }

    @Test
    void searchAllAnimals_AsClient_ShouldReturnForbidden() {
        authAsClient()
                .queryParam("page", 0)
                .queryParam("size", 5)
                .when()
                .get("/searchAll")
                .then()
                .statusCode(403);
    }

    @Test
    void getAnimalByRg_AsManager_ExistingRg_ShouldReturnAnimal() {
        authAsManager()
                .when()
                .get("/animalRg/12345678")
                .then()
                .statusCode(200)
                .body("rg", equalTo(12345678));
    }

    @Test
    void getAnimalByRg_AsClient_ShouldReturnForbidden() {
        authAsClient()
                .when()
                .get("/animalRg/12345678")
                .then()
                .statusCode(403);
    }

    @Test
    void registerAnimal_AsManager_Success_ShouldRegisterAnimal() {
        authAsManager()
                .body(animalRequest("12345678901"))
                .when()
                .post("/register")
                .then()
                .statusCode(200)
                .body("message", equalTo("Animal Registrado!"));
    }

    @Test
    void registerAnimal_AsClient_ShouldReturnForbidden() {
        authAsClient()
                .body(animalRequest("12345678901"))
                .when()
                .post("/register")
                .then()
                .statusCode(403);
    }

    @Test
    void editAnimal_AsManager_Success_ShouldUpdateAnimal() {
        Map<String, Object> requestBody = Map.of(
                "id", animalId,
                "name", "Max",
                "age", 6,
                "race", "Golden",
                "specie", "Dog",
                "photoUrl", "http://newphoto.url",
                "responsible", Map.of("id", managerUserId, "name", "Jane Doe")
        );

        authAsManager()
                .body(requestBody)
                .when()
                .patch("/editAnimal/" + animalId)
                .then()
                .statusCode(200)
                .body("message", equalTo("Editado com Sucesso!"));
    }

    @Test
    void deleteAnimal_AsClient_ShouldReturnForbidden() {
        authAsClient()
                .when()
                .delete("/delete/" + animalId)
                .then()
                .statusCode(403);
    }
}
