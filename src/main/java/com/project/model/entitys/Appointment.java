package com.project.model.entitys;

import com.project.model.entitys.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private Usuario userEmail;

    @Column(nullable = false)
    private LocalDateTime requestedDateTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private LocalDateTime confirmedDateTime;
    private String adminNotes;

}
