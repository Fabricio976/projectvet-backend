package com.project.model.dto;

import com.project.model.entitys.Animal;

public record AnimalDTO(
        String id,
        Integer rg,
        String name,
        Integer age,
        String race,
        String specie,
        String responsible,
        String photoUrl
) {}

