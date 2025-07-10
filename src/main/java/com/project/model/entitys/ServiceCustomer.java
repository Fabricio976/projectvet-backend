package com.project.model.entitys;

import com.project.model.entitys.enums.ServicePet;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
// Ficha
public class ServiceCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date consultationDate;

    private String description;

    private ServicePet servicePet;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;
}