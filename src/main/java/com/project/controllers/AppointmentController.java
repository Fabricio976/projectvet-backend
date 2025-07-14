package com.project.controllers;

import com.project.model.dto.AppointmentConfirmationDTO;
import com.project.model.dto.AppointmentDTO;
import com.project.model.entitys.Appointment;
import com.project.model.entitys.Usuario;
import com.project.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/projectvet/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/solicitar")
    public ResponseEntity<String> requestAppointment(@RequestBody AppointmentDTO dto) {
        String response = appointmentService.requestAppointment(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm/{id}")
    public ResponseEntity<Appointment> confirmAppointment(
            @PathVariable String id,
            @RequestBody AppointmentConfirmationDTO dto) {
        return ResponseEntity.ok(
                appointmentService.confirmAppointment(id, dto.confirmedDateTime(), dto.adminNotes())
        );
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<Appointment> rejectAppointment(
            @PathVariable String id,
            @RequestParam(required = false) String adminNotes) {
        return ResponseEntity.ok(appointmentService.rejectAppointment(id, adminNotes));
    }

}