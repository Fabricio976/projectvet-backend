package com.project.model.dto;

import com.project.model.entitys.enums.ServicePet;

import java.util.Date;

public record ClinicalRecordDTO(
        String animalId,
        Date consultationDate,
        String description
) {
}
