package com.project.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AppointmentConfirmationDTO(
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime confirmedDateTime,
        String adminNotes
) {}
