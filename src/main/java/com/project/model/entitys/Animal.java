package com.project.model.entitys;

import java.util.Date;

import com.project.model.entitys.enums.ServicePet;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private int rg;
    private String name;
    private int age;
    private String race;
    private String specie;
    private Appointment appointment;

    private String photoUrl;

    @Temporal(TemporalType.DATE)
    private Date dateRegister;

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario responsible;

    @Enumerated(EnumType.STRING)
    private ServicePet servicePet;

}
