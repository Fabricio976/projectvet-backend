package com.project.model.dto;

import com.project.model.entitys.enums.ServicePet;

import java.time.LocalDateTime;

public record AppointmentDTO(
        String userEmail,
        LocalDateTime requestedDateTime,
        String requestDateTime,
        Integer animalRg,
        String serviceDetails,
        ServicePet servicePet
) {
}
