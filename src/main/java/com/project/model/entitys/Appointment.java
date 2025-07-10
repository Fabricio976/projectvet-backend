package com.project.model.entitys;

import com.project.model.entitys.enums.AppointmentStatus;
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario userEmail;

    @Column(nullable = false)
    private LocalDateTime requestedDateTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private LocalDateTime confirmedDateTime;
    private String adminNotes;

    public Appointment(Usuario userEmail, LocalDateTime requestedDateTime) {
        this.userEmail = userEmail;
        this.requestedDateTime = requestedDateTime;
        this.status = AppointmentStatus.PENDING;
    }

}
