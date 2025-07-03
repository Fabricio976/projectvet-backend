package com.project.model.dto;

import com.project.model.entitys.Imagem;
import com.project.model.entitys.enums.ServicePet;

public record RegisterAnimalDTO(
        String name,
        int age,
        String race,
        String specie,
        String cpf,
        ServicePet servicePet
) { }
