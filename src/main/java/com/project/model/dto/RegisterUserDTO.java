package com.project.model.dto;

import com.project.model.entitys.enums.RoleName;

public record RegisterUserDTO(
        String name,
        String email,
        String password,
        String cpf,
        RoleName role,
        String address,
        String phone
) {
}
