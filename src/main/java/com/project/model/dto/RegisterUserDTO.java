package com.project.model.dto;

import com.project.model.entitys.enums.Role;

public record RegisterUserDTO(
        String name,
        String email,
        String password,
        String cpf,
        Role role,
        String address,
        String phone) {
}
