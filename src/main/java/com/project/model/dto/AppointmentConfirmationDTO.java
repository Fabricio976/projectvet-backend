package com.project.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AppointmentConfirmationDTO(
        LocalDateTime confirmedDateTime,
        String adminNotes
) {}
