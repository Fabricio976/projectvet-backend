package com.project.model.entitys;

import java.util.Date;

import com.project.model.entitys.enums.ServicePet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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

    @Temporal(TemporalType.DATE)
    private Date dateRegister;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario responsible;

    private ServicePet servicePet;

}
