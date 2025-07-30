package com.project.services;

import com.project.model.dto.ClinicalRecordDTO;
import com.project.model.entitys.Animal;
import com.project.model.entitys.ClinicalRecord;
import com.project.model.exeptions.AnimalNotFoundException;
import com.project.model.exeptions.ClinicalRecordNotFoundException;
import com.project.model.repositorys.AnimalRepository;
import com.project.model.repositorys.ClinicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClinicalRecordService {

    private final AnimalRepository animalRepository;

    private final ClinicalRecordRepository clinicalRecordRepository;

    public ClinicalRecordService(AnimalRepository animalRepository, ClinicalRecordRepository clinicalRecordRepository) {
        this.animalRepository = animalRepository;
        this.clinicalRecordRepository = clinicalRecordRepository;
    }

    public ClinicalRecord createRecord(ClinicalRecordDTO dto) {
        Animal animal = animalRepository.findByRg(dto.rg())
                .orElseThrow(() -> new AnimalNotFoundException("Animal não encontrado"));

        ClinicalRecord record = new ClinicalRecord();
        record.setAnimal(animal);
        record.setConsultationDate(dto.consultationDate());
        record.setDescription(dto.description());

        return clinicalRecordRepository.save(record);
    }

    public String updateRecord(String id, ClinicalRecordDTO dto) {
        ClinicalRecord record = clinicalRecordRepository.findById(id)
                .orElseThrow(() -> new ClinicalRecordNotFoundException("Ficha clínica não encontrada"));

        Animal animal = animalRepository.findByRg(dto.rg())
                .orElseThrow(() -> new AnimalNotFoundException("Animal não encontrado"));

        record.setAnimal(animal);
        record.setConsultationDate(dto.consultationDate());
        record.setDescription(dto.description());

        clinicalRecordRepository.save(record);
        return "Ficha atualizada com sucesso!";
    }

    public String deleteRecord(String  id) {
        ClinicalRecord record = clinicalRecordRepository.findById(id)
                .orElseThrow(() -> new ClinicalRecordNotFoundException("Ficha clínica não encontrada"));

        clinicalRecordRepository.delete(record);
        return "Ficha excluída com sucesso!";
    }

}
