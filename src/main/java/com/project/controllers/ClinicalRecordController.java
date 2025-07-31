package com.project.controllers;

import com.project.model.dto.ClinicalRecordDTO;
import com.project.model.entitys.Animal;
import com.project.model.entitys.ClinicalRecord;
import com.project.model.exeptions.AnimalNotFoundException;
import com.project.model.exeptions.ClinicalRecordNotFoundException;
import com.project.model.repositorys.AnimalRepository;
import com.project.model.repositorys.ClinicalRecordRepository;
import com.project.services.ClinicalRecordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/projectvet/clinical-records")
public class ClinicalRecordController {

    private final ClinicalRecordService clinicalRecordService;

    private final ClinicalRecordRepository clinicalRecordRepository;

    private final AnimalRepository animalRepository;

    public ClinicalRecordController(ClinicalRecordService clinicalRecordService, ClinicalRecordRepository clinicalRecordRepository, AnimalRepository animalRepository) {
        this.clinicalRecordService = clinicalRecordService;
        this.clinicalRecordRepository = clinicalRecordRepository;
        this.animalRepository = animalRepository;
    }

    //  POST /projectvet/clinical-records
    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody @Valid ClinicalRecordDTO dto) {
        ClinicalRecord result = clinicalRecordService.createRecord(dto);
        return ResponseEntity.ok(Map.of("message", "Ficha registrada com sucesso!", "id", result.getId()));
    }

    //  PATCH /projectvet/clinical-records/{id}
    @PatchMapping("/{id}")
    public String updateRecord(@PathVariable String id, @RequestBody ClinicalRecordDTO dto) {
        ClinicalRecord record = clinicalRecordRepository.findById(id)
                .orElseThrow(() -> new ClinicalRecordNotFoundException("Ficha clínica não encontrada"));

        Animal animal = animalRepository.findByRg(dto.rg())
                .orElseThrow(() -> new AnimalNotFoundException("Animal não encontrado"));

        record.setAnimal(animal);
        record.setConsultationDate(dto.consultationDate());
        record.setDescription(dto.description());

        clinicalRecordRepository.save(record);
        return "Ficha de consulta atualizada com sucesso!";
    }

    //  DELETE /projectvet/clinical-records/{id}
    @DeleteMapping("/{id}")
    public String deleteRecord(@PathVariable String id) {
        ClinicalRecord record = clinicalRecordRepository.findById(id)
                .orElseThrow(() -> new ClinicalRecordNotFoundException("Ficha clínica não encontrada"));

        clinicalRecordRepository.delete(record);
        return "Ficha de consulta excluída com sucesso!";
    }

}
