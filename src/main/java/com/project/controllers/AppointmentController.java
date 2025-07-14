package com.project.controllers;

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

    @PostMapping
    public ResponseEntity<Appointment> requestAppointment(@RequestBody AppointmentDTO dto) {
        return ResponseEntity.ok(appointmentService.requestAppointment(dto));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Appointment> confirmAppointment(
            @PathVariable String id,
            @RequestParam LocalDateTime confirmedDateTime,
            @RequestParam(required = false) String adminNotes) {
        return ResponseEntity.ok(appointmentService.confirmAppointment(id, confirmedDateTime, adminNotes));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Appointment> rejectAppointment(
            @PathVariable String id,
            @RequestParam(required = false) String adminNotes) {
        return ResponseEntity.ok(appointmentService.rejectAppointment(id, adminNotes));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Appointment>> getPendingAppointments() {
        return ResponseEntity.ok(appointmentService.getPendingAppointments());
    }

    @GetMapping("/user")
    public ResponseEntity<List<Appointment>> getUserAppointments(@AuthenticationPrincipal Usuario user) {
        return ResponseEntity.ok(appointmentService.getUserAppointments(user));
    }
}