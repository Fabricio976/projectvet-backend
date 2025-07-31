package com.project.model.dto;

import java.util.Date;

public record ClinicalRecordResponseDTO(
        String id,
        Integer rg,
        Date consultationDate,
        String description) {
}
