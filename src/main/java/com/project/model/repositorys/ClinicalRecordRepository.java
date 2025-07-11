package com.project.model.repositorys;

import com.project.model.entitys.Animal;
import com.project.model.entitys.ClinicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClinicalRecordRepository extends JpaRepository<ClinicalRecord, String> {
    List<ClinicalRecord> findByAnimalId(String animalId);

}
