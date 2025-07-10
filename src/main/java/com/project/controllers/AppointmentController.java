package com.project.controllers;

import com.project.model.entitys.Appointment;
import com.project.model.entitys.Usuario;
import com.project.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/projectvet/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/request")
    public ResponseEntity<Appointment> requestAppointment(
            @RequestParam Usuario userEmail,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestedDateTime) {
        Appointment appointment = appointmentService.requestAppointment(userEmail, requestedDateTime);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Appointment> confirmAppointment(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime confirmedDateTime,
            @RequestParam(required = false) String adminNotes) {
        Appointment appointment = appointmentService.confirmAppointment(id, confirmedDateTime, adminNotes);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Appointment> rejectAppointment(
            @PathVariable String id,
            @RequestParam(required = false) String adminNotes) {
        Appointment appointment = appointmentService.rejectAppointment(id, adminNotes);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Appointment>> getPendingAppointments() {
        List<Appointment> appointments = appointmentService.getPendingAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<Appointment>> getUserAppointments(@PathVariable Usuario email) {
        List<Appointment> appointments = appointmentService.getUserAppointments(email);
        return ResponseEntity.ok(appointments);
    }
}