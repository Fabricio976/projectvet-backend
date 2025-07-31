package com.project.model.dto;

import java.time.LocalDateTime;

public record AppointmentConfirmationDTO(
        LocalDateTime confirmedDateTime,
        String adminNotes
) {}
