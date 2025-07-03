package com.project.controllers;

import com.project.model.dto.ClinicalRecordDTO;
import com.project.model.entitys.ClinicalRecord;
import com.project.services.details.ClinicalRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projectvet/clinical-records")
public class ClinicalRecordController {

    @Autowired
    private ClinicalRecordService clinicalRecordService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, String>> create(@RequestBody @Valid ClinicalRecordDTO dto) {
        String result = clinicalRecordService.createRecord(dto);
        return ResponseEntity.ok(Map.of("message", result));
    }

    @GetMapping("/animal/{animalId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'CLIENT')")
    public List<ClinicalRecord> listByAnimal(@PathVariable String animalId) {
        return clinicalRecordService.listByAnimal(animalId);
    }
}
