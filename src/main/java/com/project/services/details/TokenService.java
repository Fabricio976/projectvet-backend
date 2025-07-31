package com.project.services.details;

import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.project.model.entitys.Usuario;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(Usuario user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("projectvet")
                    .withSubject(user.getId())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error na geração do token!", exception);
        }
    }

    public String validateToken(String token) {
        return createAlgorithm()
                .map(algorithm -> JWT.require(algorithm)
                        .withIssuer("projectvet")
                        .build()
                        .verify(token)
                        .getSubject())
                .orElseThrow(() -> new RuntimeException("Token inválido"));
    }

    private Optional<Algorithm> createAlgorithm() {
        try {
            return Optional.of(Algorithm.HMAC256(secret));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
    }

}