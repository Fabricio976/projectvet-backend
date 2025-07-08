package com.project.model.dto;

import com.project.model.entitys.Role;

import java.util.List;

public record RecoveryUserDto(
        Long id,
        String email,
        List<Role> roles
) {
}
