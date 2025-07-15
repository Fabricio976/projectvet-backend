package com.project.TestsRest;

import io.restassured.http.ContentType;

import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class TestUtils {

    public static UsuarioInfo criarUsuarioValido(String role) {
        String cpf = gerarCpfValido();
        String email = gerarEmailAleatorio();
        String senha = "123456"; // senha padrão para teste

        Map<String, Object> request = Map.of(
                "name", "Usuário Teste",
                "email", email,
                "cpf", cpf,
                "password", senha
        );

        String path = role.equalsIgnoreCase("MANAGER") ? "/projectvet/register/manager" : "/projectvet/register/client";

        String id = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(path)
                .then()
                .statusCode(200)
                .extract()
                .path("message"); // ou ID se retornar isso

        return new UsuarioInfo(id, cpf, email, senha);
    }

    public static String obterToken(String email, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", email, "password", password))
                .when()
                .post("/projectvet/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    public static String criarAnimalComUsuario(String token, String cpf) {
        Map<String, Object> animal = Map.of(
                "name", "Rex",
                "age", 5,
                "race", "Labrador",
                "specie", "Dog",
                "photoUrl", "http://photo.url",
                "responsible", cpf
        );

        return given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(animal)
                .when()
                .post("/projectvet/animal/register")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    // Gerador simples para CPF fake válido
    public static String gerarCpfValido() {
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

    public static String gerarEmailAleatorio() {
        return "usuario" + System.currentTimeMillis() + "@teste.com";
    }
}
