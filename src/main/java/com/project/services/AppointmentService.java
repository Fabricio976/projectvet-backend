package com.project.services;

import com.project.model.entitys.Appointment;
import com.project.model.entitys.Usuario;
import com.project.model.entitys.enums.AppointmentStatus;
import com.project.model.repositorys.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String adminEmail;

    public Appointment requestAppointment(Usuario userEmail, LocalDateTime requestedDateTime) {
        Appointment appointment = new Appointment(userEmail, requestedDateTime);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        Map<String, Object> properties = new HashMap<>();
        properties.put("userEmail", userEmail);
        properties.put("requestedDateTime", requestedDateTime.toString());
        properties.put("appointmentId", savedAppointment.getId());

        emailService.enviarEmailTemplate(
                adminEmail,
                "Nova Solicitação de Consulta",
                properties
        );

        return savedAppointment;
    }

    public Appointment confirmAppointment(String appointmentId, LocalDateTime confirmedDateTime, String adminNotes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setConfirmedDateTime(confirmedDateTime);
        appointment.setAdminNotes(adminNotes);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        Map<String, Object> properties = new HashMap<>();
        properties.put("confirmedDateTime", confirmedDateTime.toString());
        properties.put("adminNotes", adminNotes != null ? adminNotes : "");

        emailService.enviarEmailTemplate(
                adminEmail,
                "Confirmação de Consulta",
                properties
        );

        return updatedAppointment;
    }

    public Appointment rejectAppointment(String appointmentId, String adminNotes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.REJECTED);
        appointment.setAdminNotes(adminNotes);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        Map<String, Object> properties = new HashMap<>();
        properties.put("adminNotes", adminNotes != null ? adminNotes : "");

        emailService.enviarEmailTemplate(
                adminEmail,
                "Rejeição de Consulta",
                properties
        );

        return updatedAppointment;
    }

    public List<Appointment> getPendingAppointments() {
        return appointmentRepository.findByStatus(AppointmentStatus.PENDING);
    }

    public List<Appointment> getUserAppointments(Usuario userEmail) {
        return appointmentRepository.findByUserEmail(userEmail);
    }
}