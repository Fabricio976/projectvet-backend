package com.project.model.dto;

import com.project.model.entitys.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record RegisterUserDTO(
        String name,

        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail é obrigatório")
        String email,
        String password,

        String cpf,
        RoleName role,
        String address,
        String phone
) {
}
