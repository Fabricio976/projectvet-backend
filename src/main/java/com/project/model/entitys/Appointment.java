package com.project.model.entitys;

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
    @JoinColumn(name = "email")
    private Usuario userEmail;

    @Column(nullable = false)
    private LocalDateTime requestedDateTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private String serviceDetails;

    @Enumerated(EnumType.STRING)
    private ServicePet servicePet;

    @ManyToOne
    @JoinColumn(name = "animal_rg")
    private Animal animalRg;

    private LocalDateTime confirmedDateTime;
    private String adminNotes;

    public Appointment(Usuario userEmail, LocalDateTime requestedDateTime, Animal animalRg) {
        this.userEmail = userEmail;
        this.requestedDateTime = requestedDateTime;
        this.status = AppointmentStatus.PENDING;
        this.animalRg = animalRg;
    }

}
