package com.project.model.entitys;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.model.entitys.enums.AppointmentStatus;
import com.project.model.entitys.enums.ServicePet;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario userEmail;

    @Column(nullable = false)
    private LocalDateTime requestedDateTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private String serviceDetails;

    @Enumerated(EnumType.STRING)
    private ServicePet servicePet;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    @JsonBackReference
    private Animal animalAppointment;

    private LocalDateTime confirmedDateTime;
    private String adminNotes;

    public Appointment(Usuario userEmail, LocalDateTime requestedDateTime, Animal animalAppointment) {
        this.userEmail = userEmail;
        this.requestedDateTime = requestedDateTime;
        this.status = AppointmentStatus.PENDING;
        this.animalAppointment = animalAppointment;
    }

}
