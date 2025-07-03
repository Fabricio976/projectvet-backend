package com.project.services.details;
import com.project.model.entitys.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.project.model.entitys.Usuario;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(Usuario user) {
        return createAlgorithm()
                .map(algorithm -> {
                    // Transforma as roles em authorities com "ROLE_"
                    List<String> authorities = user.getRole().stream()
                            .map(Role::getAuthority) // ex: "ROLE_MANAGER"
                            .collect(Collectors.toList());

                    return JWT.create()
                            .withIssuer("projectvet")
                            .withSubject(user.getId())
                            .withClaim("authorities", authorities)
                            .withExpiresAt(genExpirationDate())
                            .sign(algorithm);
                })
                .orElseThrow(() -> new RuntimeException("Erro na geração do token"));
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