package com.project.TestsRest;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ClinicalRecordIntegrationTest extends BaseIntegrationTest {

    private static Integer animalRg;

    @BeforeAll
    public static void setupOnce() {
        animalRg = Integer.valueOf(String.valueOf(new Random().ints(10000000, 99999999).distinct().findFirst().getAsInt()));
    }

    // Unico animal para o test
    @BeforeEach
    public void setupAnimal() {
        Map<String, Object> animal = new HashMap<>();
        animal.put("id", UUID.randomUUID().toString());
        animal.put("rg", animalRg);
        animal.put("name", "Joaquin");
        animal.put("age", 4);
        animal.put("race", "Golden");
        animal.put("specie", "Dog");
        animal.put("responsible", clientCpf);
        animal.put("photoUrl", "");
        animal.put("dateRegister", LocalDate.now().toString());

        animalRg = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .body(animal)
                .when()
                .post("/animals/register")
                .then()
                .statusCode(200)
                .extract()
                .path("animal.rg");
    }

    @Test
    public void testCriaClinicalRecord_Success() {
        Map<String, Object> clinicalDTO = new HashMap<>();
        clinicalDTO.put("rg", animalRg);
        clinicalDTO.put("consultationDate", LocalDate.now().toString());
        clinicalDTO.put("description", "Animal com dor na pata dianteira.");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + managerToken)
                .body(clinicalDTO)
                .when()
                .post("/clinical-records/create")
                .then()
                .statusCode(200)
                .body("message", equalTo("Ficha registrada com sucesso!"));
    }

    @Test
    public void testUpdateClinicalRecord_Success() {
        // Primeiro cria a ficha
        Map<String, Object> clinicalDTO = new HashMap<>();
        clinicalDTO.put("rg", animalRg);
        clinicalDTO.put("consultationDate", LocalDate.now().toString());
        clinicalDTO.put("description", "Primeira consulta.");

        String recordId = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + managerToken)
                .body(clinicalDTO)
                .when()
                .post("/clinical-records/create")
                .then()
                .statusCode(200)
                .extract()
                .path("id"); // supondo que o ID seja retornado na resposta

        // Atualiza a ficha
        clinicalDTO.put("description", "Consulta atualizada: dor melhorou.");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + managerToken)
                .body(clinicalDTO)
                .when()
                .patch("/clinical-records/edit/" + recordId)
                .then()
                .statusCode(200)
                .body(equalTo("Ficha de consulta atualizada com sucesso!"));
    }

    @Test
    public void testDeleteClinicalRecord_Success() {
        // Cria ficha para deletar
        Map<String, Object> clinicalDTO = new HashMap<>();
        clinicalDTO.put("rg", animalRg);
        clinicalDTO.put("consultationDate", LocalDate.now().toString());
        clinicalDTO.put("description", "Ficha para deletar.");

        String recordId = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + managerToken)
                .body(clinicalDTO)
                .when()
                .post("/clinical-records/create")
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + managerToken)
                .when()
                .delete("/clinical-records/delete/" + recordId)
                .then()
                .statusCode(200)
                .body(equalTo("Ficha de consulta excluída com sucesso!"));
    }

    @Test
    public void testCreateClinicalRecord_AnimalNaoEncontrado() {
        Map<String, Object> clinicalDTO = new HashMap<>();
        clinicalDTO.put("rg", 2131412);
        clinicalDTO.put("consultationDate", LocalDate.now().toString());
        clinicalDTO.put("description", "Tentando cadastrar com animal inexistente");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + managerToken)
                .body(clinicalDTO)
                .when()
                .post("/clinical-records/create")
                .then()
                .statusCode(404)
                .body((equalTo("Animal não encontrado")));
    }

    @Test
    public void testCreateClinicalRecord_ClienteTentaCadastrarFicha() {
        Map<String, Object> clinicalDTO = new HashMap<>();
        clinicalDTO.put("rg", animalRg);
        clinicalDTO.put("consultationDate", LocalDate.now().toString());
        clinicalDTO.put("description", "Cliente tentando cadastrar ficha");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + clientToken)
                .body(clinicalDTO)
                .when()
                .post("/clinical-records/create")
                .then()
                .statusCode(403);
    }

}