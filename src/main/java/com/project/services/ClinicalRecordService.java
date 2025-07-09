package com.project.services;

import com.project.model.dto.ClinicalRecordDTO;
import com.project.model.entitys.Animal;
import com.project.model.entitys.ClinicalRecord;
import com.project.model.exeptions.AnimalNotFoundException;
import com.project.model.repositorys.AnimalRepository;
import com.project.model.repositorys.ClinicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClinicalRecordService {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private ClinicalRecordRepository clinicalRecordRepository;

    public String createRecord(ClinicalRecordDTO dto) {
        Animal animal = animalRepository.findById(dto.animalId())
                .orElseThrow(() -> new AnimalNotFoundException("Animal não encontrado"));

        ClinicalRecord record = new ClinicalRecord();
        record.setAnimal(animal);
        record.setConsultationDate(dto.consultationDate());
        record.setDescription(dto.description());

        clinicalRecordRepository.save(record);
        return "Ficha de consulta registrada com sucesso!";
    }

    public List<ClinicalRecord> listByAnimal(String animalId) {
        return clinicalRecordRepository.findByAnimalId(animalId);
    }
}
