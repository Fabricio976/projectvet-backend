package com.project.services;

import com.project.model.dto.AppointmentDTO;
import com.project.model.entitys.Animal;
import com.project.model.entitys.Appointment;
import com.project.model.entitys.Usuario;
import com.project.model.entitys.enums.AppointmentStatus;
import com.project.model.repositorys.AnimalRepository;
import com.project.model.repositorys.AppointmentRepository;
import com.project.model.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String adminEmail;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");


    public String requestAppointment(AppointmentDTO data) {
        UserDetails user = usuarioRepository.findByEmail(data.userEmail());
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Animal animal = animalRepository.findByRg(data.animalRg())
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));

        Appointment appointment = new Appointment();
        appointment.setUserEmail((Usuario) user);
        appointment.setRequestedDateTime(data.requestedDateTime());
        appointment.setAnimalAppointment(animal);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setServiceDetails(data.serviceDetails());
        appointment.setServicePet(data.servicePet());

        Appointment savedAppointment = appointmentRepository.save(appointment);
        String formatted = data.requestedDateTime().format(formatter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("userEmail", data.userEmail());
        properties.put("requestedDateTime", formatted);
        properties.put("appointmentId", savedAppointment.getId());

        emailService.enviarEmailTemplate(
                adminEmail,
                "Nova Solicitação de Consulta",
                properties
        );

        return "Solicitação enviada, aguarde confirmação";
    }

    public Appointment confirmAppointment(String appointmentId, LocalDateTime confirmedDateTime, String adminNotes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setConfirmedDateTime(confirmedDateTime);
        appointment.setAdminNotes(adminNotes);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        Map<String, Object> properties = new HashMap<>();
        properties.put("confirmedDateTime", confirmedDateTime.format(formatter));
        properties.put("adminNotes", "");

        emailService.enviarEmailTemplate(
                appointment.getUserEmail().getEmail(),
                "Confirmação de Atendimento",
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
                appointment.getUserEmail().getEmail(),
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