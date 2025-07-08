package com.project.model.dto;

import com.project.model.entitys.Role;

import java.util.List;

public record RecoveryUserDTO(
        Long id,
        String email,
        List<Role> roles
) {
}
