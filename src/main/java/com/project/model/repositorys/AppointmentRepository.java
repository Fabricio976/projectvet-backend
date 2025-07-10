package com.project.model.repositorys;

import com.project.model.entitys.Appointment;
import com.project.model.entitys.Usuario;
import com.project.model.entitys.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, String> {

    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByUserEmail(Usuario userEmail);
}