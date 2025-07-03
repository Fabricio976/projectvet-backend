package com.project.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.model.entitys.enums.ServicePet;

public record RegisterAnimalDTO(
        String name,
        int age,
        String race,
        String specie,
        String responsible,
        String photoUrl,
        ServicePet servicePet
) { }
