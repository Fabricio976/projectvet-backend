package com.project.TestsRest;


import com.project.model.dto.RegisterUserDTO;
import com.project.model.dto.AuthenticationDTO;
import com.project.model.entitys.enums.RoleName;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AnimalIntegrationTest extends BaseIntegrationTest {

    private Random random = new Random();

    private java.util.stream.IntStream generateRgStream() {
        return random.ints(10000000, 99999999).distinct().limit(1000);
    }

    @Test
    public void testRegisterAnimal_Success() {

        Map<String, Object> newAnimal = new HashMap<>();
        String animalId = UUID.randomUUID().toString();
        newAnimal.put("id", animalId);
        newAnimal.put("rg", generateRgStream().findFirst().getAsInt());
        newAnimal.put("name", "Toto");
        newAnimal.put("age", 5);
        newAnimal.put("race", "Puddle");
        newAnimal.put("specie", "Dog");
        newAnimal.put("responsible", clientCpf);
        newAnimal.put("photoUrl", "");
        newAnimal.put("dateRegister", new Date().toString());

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .body(newAnimal)
                .when()
                .post("/animals")
                .then()
                .statusCode(200)
                .body("message", equalTo("Animal registrado com sucesso!"))
                .body("animal.id", notNullValue())
                .body("animal.rg", notNullValue())
                .body("animal.name", notNullValue());
    }

    @Test
    public void testListPorClient() {
        Map<String, Object> newAnimal = new HashMap<>();
        String animalId = UUID.randomUUID().toString();
        newAnimal.put("id", animalId);
        newAnimal.put("rg", generateRgStream().findFirst().getAsInt());
        newAnimal.put("name", "Jurubeba");
        newAnimal.put("age", 5);
        newAnimal.put("race", "Raciado");
        newAnimal.put("specie", "Cavalo");
        newAnimal.put("responsible", clientCpf);
        newAnimal.put("photoUrl", null);
        newAnimal.put("dateRegister", new Date().toString());

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .body(newAnimal)
                .when()
                .post("/animals")
                .then()
                .statusCode(200);

        // Lista como cliente
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .get("/animals?page=0&size=5")
                .then()
                .statusCode(200)
                .body("page.totalElements", greaterThanOrEqualTo(1))
                .body("content.name", hasItem("Jurubeba"));
    }

    @Test
    public void testListAllAnimals_ParaManager() {
        // Registra animal para cliente existente
        Map<String, Object> animal1 = new HashMap<>();
        String animal1Id = UUID.randomUUID().toString();
        animal1.put("id", animal1Id);
        animal1.put("rg", generateRgStream().findFirst().getAsInt());
        animal1.put("name", "Juarez");
        animal1.put("age", 5);
        animal1.put("race", "Labrador");
        animal1.put("specie", "Dog");
        animal1.put("photoUrl", "");
        animal1.put("responsible", clientCpf);
        animal1.put("dateRegister", new Date().toString());

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .body(animal1)
                .when()
                .post("/animals")
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
                .post("/auth/client")
                .then()
                .statusCode(200);

        // Login
        AuthenticationDTO authDTO = new AuthenticationDTO(anotherEmail, "password123");
        String anotherToken = given()
                .contentType(ContentType.JSON)
                .body(authDTO)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        // Registra animal para novo cliente
        Map<String, Object> animal2 = new HashMap<>();
        String animal2Id = UUID.randomUUID().toString();
        animal2.put("id", animal2Id);
        animal2.put("rg", generateRgStream().findFirst().getAsInt());
        animal2.put("name", "Luna");
        animal2.put("age", 5);
        animal2.put("race", "Não definida");
        animal2.put("specie", "Cat");
        animal2.put("photoUrl", "");
        animal2.put("responsible", anotherCpf);
        animal2.put("dateRegister", new Date().toString());


        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + anotherToken)
                .body(animal2)
                .when()
                .post("/animals")
                .then()
                .statusCode(200);

        // Lista como adm (espera ver todos)
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .get("/animals?page=0&size=5")
                .then()
                .statusCode(200)
                .body("page.totalElements", greaterThanOrEqualTo(2))
                .body("content.name", hasItems("Juarez", "Luna"));
    }


    @Test
    public void testListAnimal_ByCpf() {

        Map<String, Object> newAnimal = new HashMap<>();
        String animalId = UUID.randomUUID().toString();
        newAnimal.put("id", animalId);
        newAnimal.put("rg", generateRgStream().findFirst().getAsInt());
        newAnimal.put("name", "Rex");
        newAnimal.put("age", 5);
        newAnimal.put("race", "Caramelo");
        newAnimal.put("specie", "Dog");
        newAnimal.put("photoUrl", "");
        newAnimal.put("responsible", clientCpf);
        newAnimal.put("dateRegister", new Date().toString());

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .body(newAnimal)
                .when()
                .post("/animals")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .get("/animals/by-user/" + clientCpf)
                .then()
                .statusCode(200)
                .body("name", hasItem("Rex"));
    }

    @Test
    public void testEdit_Animal() {
        Map<String, Object> newAnimal = new HashMap<>();
        String animalId = UUID.randomUUID().toString();
        newAnimal.put("id", animalId);
        newAnimal.put("rg", generateRgStream().findFirst().getAsInt());
        newAnimal.put("name", "Cleiton");
        newAnimal.put("age", 5);
        newAnimal.put("race", "BulDog");
        newAnimal.put("specie", "Dog");
        newAnimal.put("photoUrl", "");
        newAnimal.put("responsible", clientCpf);
        newAnimal.put("dateRegister", new Date().toString());

        String idCriat = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .body(newAnimal)
                .when()
                .post("/animals")
                .then()
                .statusCode(200)
                .extract()
                .path("animal.id");


        // Monta o objeto editado (mesmo ID, mas com novos valores)
        Map<String, Object> updatedAnimal = new HashMap<>();
        updatedAnimal.put("id", idCriat);
        updatedAnimal.put("name", "Bolota");
        updatedAnimal.put("age", 4);
        updatedAnimal.put("race", "BulDog");
        updatedAnimal.put("specie", "Dog");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .body(updatedAnimal)
                .when()
                .patch("/animals/" + idCriat)
                .then()
                .statusCode(200)
                .body("message", equalTo("Editado com Sucesso!"));


        // Verifica se foi editado corretamente
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .get("/animals/" + idCriat)
                .then()
                .statusCode(200)
                .body("name", equalTo("Bolota"));
    }

    @Test
    public void testSeach_PorRgAnimal() {
        Map<String, Object> newAnimal = new HashMap<>();
        String animalId = UUID.randomUUID().toString();
        newAnimal.put("id", animalId);
        newAnimal.put("rg", generateRgStream().findFirst().getAsInt());
        newAnimal.put("name", "Pudim");
        newAnimal.put("age", 5);
        newAnimal.put("race", "BulDog");
        newAnimal.put("specie", "Dog");
        newAnimal.put("photoUrl", "");
        newAnimal.put("responsible", clientCpf);
        newAnimal.put("dateRegister", new Date().toString());

        Integer rgCriat = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .body(newAnimal)
                .when()
                .post("/animals")
                .then()
                .statusCode(200)
                .extract()
                .path("animal.rg");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .get("/animals/by-rg/" + rgCriat)
                .then()
                .statusCode(200)
                .body("name", equalTo("Pudim"));


    }

}
