package com.project.model.dto;

import java.util.Date;

public record ClinicalRecordDTO(
        Integer rg,
        Date consultationDate,
        String description
) { }
