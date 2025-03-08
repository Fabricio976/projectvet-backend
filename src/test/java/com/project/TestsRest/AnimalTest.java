package com.project.TestsRest;

import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.project.model.dto.AuthenticationDTO;
import com.project.model.dto.RegisterAnimalDTO;
import com.project.model.enums.ServicePet;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class AnimalTest extends AuthenticationTestBase {

    @Test
    public void testRegisterAnimalSuccess() {


        AuthenticationDTO loginData = new AuthenticationDTO("test_pedroluiz@example.com", "password123");

        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(loginData)
                .when()
                .post("/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("token", notNullValue())
                .body("userId", notNullValue())
                .extract()
                .response();


        String token = loginResponse.jsonPath().getString("token");
        String responsible = loginResponse.jsonPath().getString("userId");

        System.out.println("Token gerado: " + token);

        RegisterAnimalDTO animal = new RegisterAnimalDTO(
                "Buddy",
                2,
                "Golden Retriever",
                "Dog",
                responsible,
                ServicePet.PETCLINIC);

        given()
                .auth()
                .oauth2(token) // Usando o token de autenticação
                .contentType(ContentType.JSON)
                .body(animal)
                .when()
                .post("/animal/register")
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}