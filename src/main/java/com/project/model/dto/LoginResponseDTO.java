package com.project.model.dto;

public record LoginResponseDTO(String token, String userId, String userNome, java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> authorities) {}