package com.project.controllers;

import com.project.model.dto.AppointmentConfirmationDTO;
import com.project.model.dto.AppointmentDTO;
import com.project.model.entitys.Appointment;
import com.project.services.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projectvet/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    //  POST /projectvet/appointments
    @PostMapping
    public ResponseEntity<String> requestAppointment(@RequestBody AppointmentDTO dto) {
        String response = appointmentService.requestAppointment(dto);
        return ResponseEntity.ok(response);
    }

    //  PUT /projectvet/appointments/{id}/confirmation
    @PutMapping("/{id}/confimation")
    public ResponseEntity<Appointment> confirmAppointment(
            @PathVariable String id,
            @RequestBody AppointmentConfirmationDTO dto) {
        return ResponseEntity.ok(
                appointmentService.confirmAppointment(id, dto.confirmedDateTime(), dto.adminNotes())
        );
    }

    //  POST /projectvet/appointments
    @PutMapping("/{id}/rejection")
    public ResponseEntity<Appointment> rejectAppointment(
            @PathVariable String id,
            @RequestParam(required = false) String adminNotes) {
        return ResponseEntity.ok(appointmentService.rejectAppointment(id, adminNotes));
    }

}