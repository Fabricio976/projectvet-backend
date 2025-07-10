package com.project.TestUnit.Service;


import com.project.model.dto.ClinicalRecordDTO;
import com.project.model.entitys.Animal;
import com.project.model.entitys.ClinicalRecord;
import com.project.model.exeptions.AnimalNotFoundException;
import com.project.model.exeptions.ClinicalRecordNotFoundException;
import com.project.model.repositorys.AnimalRepository;
import com.project.model.repositorys.ClinicalRecordRepository;
import com.project.services.ClinicalRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClinicalRecordServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ClinicalRecordRepository clinicalRecordRepository;

    @InjectMocks
    private ClinicalRecordService clinicalRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRecordSuccess() {
        ClinicalRecordDTO dto = new ClinicalRecordDTO(new Date(),"hj", "animal123");
        Animal animal = new Animal();
        when(animalRepository.findById("animal123")).thenReturn(Optional.of(animal));

        String result = clinicalRecordService.createRecord(dto);

        assertEquals("Ficha registrada com sucesso!", result);
        verify(clinicalRecordRepository).save(any(ClinicalRecord.class));
    }

    @Test
    void testCreateRecordAnimalNotFound() {
        ClinicalRecordDTO dto = new ClinicalRecordDTO( new Date(),"invalido", "Sintomas");
        when(animalRepository.findById("invalido")).thenReturn(Optional.empty());

        assertThrows(AnimalNotFoundException.class, () -> clinicalRecordService.createRecord(dto));
        verify(clinicalRecordRepository, never()).save(any());
    }

    @Test
    void testUpdateRecordSuccess() {
        ClinicalRecord record = new ClinicalRecord();
        record.setId("rec123");

        ClinicalRecordDTO dto = new ClinicalRecordDTO(new Date(),"animal456", "Atualizado");
        Animal animal = new Animal();

        when(clinicalRecordRepository.findById("rec123")).thenReturn(Optional.of(record));
        when(animalRepository.findById("animal456")).thenReturn(Optional.of(animal));

        String result = clinicalRecordService.updateRecord("rec123", dto);

        assertEquals("Ficha atualizada com sucesso!", result);
        assertEquals(animal, record.getAnimal());
        assertEquals("Atualizado", record.getDescription());
        verify(clinicalRecordRepository).save(record);
    }

    @Test
    void testUpdateRecordNotFound() {
        ClinicalRecordDTO dto = new ClinicalRecordDTO(new Date(),"animal456", "Atualizado");
        when(clinicalRecordRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(ClinicalRecordNotFoundException.class, () -> clinicalRecordService.updateRecord("inexistente", dto));
    }

    @Test
    void testUpdateRecordAnimalNotFound() {
        ClinicalRecord record = new ClinicalRecord();
        record.setId("rec123");
        ClinicalRecordDTO dto = new ClinicalRecordDTO(new Date(),"animalInexistente", "Atualizado" );

        when(clinicalRecordRepository.findById("rec123")).thenReturn(Optional.of(record));
        when(animalRepository.findById("animalInexistente")).thenReturn(Optional.empty());

        assertThrows(AnimalNotFoundException.class, () -> clinicalRecordService.updateRecord("rec123", dto));
    }

    @Test
    void testDeleteRecord() {
        doNothing().when(clinicalRecordRepository).deleteById("rec321");

        clinicalRecordService.deleteRecord("rec321");

        verify(clinicalRecordRepository).deleteById("rec321");
    }
}
