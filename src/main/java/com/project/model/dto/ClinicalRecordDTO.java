package com.project.model.dto;

import com.project.model.entitys.enums.ServicePet;

import java.util.Date;

public record ClinicalRecordDTO(
        Date consultationDate,
        String description,
        String animalId
) {
}
