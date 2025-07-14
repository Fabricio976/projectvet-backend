package com.project.model.entitys;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.model.entitys.enums.ServicePet;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private Integer rg;
    private String name;
    private int age;
    private String race;
    private String specie;

    @OneToMany(mappedBy = "animalAppointment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ClinicalRecord> clinicalRecords = new ArrayList<>();

    private String photoUrl;

    @Temporal(TemporalType.DATE)
    private Date dateRegister;

    @Enumerated(EnumType.STRING)
    private ServicePet servicePet;

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario responsible;


}
