package com.project.model.repositorys;

import com.project.model.entitys.ClinicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClinicalRecordRepository extends JpaRepository<ClinicalRecord, String> {
    List<ClinicalRecord> findByAnimalId(String animalId);
}
