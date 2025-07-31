package com.project.model.dto;

public record AnimalDTO(
        Integer rg,
        String name,
        Integer age,
        String race,
        String specie,
        String responsible,
        String photoUrl
) { }

