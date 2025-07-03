package com.project.model.dto;

import com.project.model.entitys.enums.ServicePet;

import java.util.Date;

public record AppointmentRequestDTO(
        Date appointmentDate,
        String description,
        String animalId,
        ServicePet servicePet
) {
}
