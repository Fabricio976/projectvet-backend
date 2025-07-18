package com.project.model.dto;

import com.project.model.entitys.enums.ServicePet;

import java.util.Date;

public record ClinicalRecordDTO(
        Integer rg,
        Date consultationDate,
        String description
) { }
