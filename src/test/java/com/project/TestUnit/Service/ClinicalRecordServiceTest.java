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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClinicalRecordServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ClinicalRecordRepository clinicalRecordRepository;

    @InjectMocks
    private ClinicalRecordService clinicalRecordService;

    private ClinicalRecordDTO clinicalRecordDTO;
    private Animal animal;
    private ClinicalRecord clinicalRecord;

    @BeforeEach
    void setUp() {
        Integer rg = 12345678;
        Date consultationDate = new Date();
        String description = "Consulta para check-up geral";

        clinicalRecordDTO = new ClinicalRecordDTO(rg, consultationDate, description);

        animal = new Animal();
        animal.setRg(rg);

        clinicalRecord = new ClinicalRecord();
        clinicalRecord.setId("1");
        clinicalRecord.setAnimal(animal);
        clinicalRecord.setConsultationDate(consultationDate);
        clinicalRecord.setDescription(description);
    }

    @Test
    void createRecord_Success() {
        when(animalRepository.findByRg(clinicalRecordDTO.rg())).thenReturn(Optional.of(animal));
        when(clinicalRecordRepository.save(any(ClinicalRecord.class))).thenReturn(clinicalRecord);

        ArgumentCaptor<ClinicalRecord> recordCaptor = ArgumentCaptor.forClass(ClinicalRecord.class);

        String result = clinicalRecordService.createRecord(clinicalRecordDTO);

        assertEquals("Ficha registrada com sucesso!", result);
        verify(animalRepository).findByRg(clinicalRecordDTO.rg());
        verify(clinicalRecordRepository).save(recordCaptor.capture());

        ClinicalRecord savedRecord = recordCaptor.getValue();
        assertEquals(animal, savedRecord.getAnimal());
        assertEquals(clinicalRecordDTO.consultationDate(), savedRecord.getConsultationDate());
        assertEquals(clinicalRecordDTO.description(), savedRecord.getDescription());
    }

    @Test
    void createRecord_AnimalNaoEncontrado_RetornaAnimalNotFoundException() {
        when(animalRepository.findByRg(clinicalRecordDTO.rg())).thenReturn(Optional.empty());

        AnimalNotFoundException exception = assertThrows(AnimalNotFoundException.class, () -> {
            clinicalRecordService.createRecord(clinicalRecordDTO);
        });

        assertEquals("Animal não encontrado", exception.getMessage());
        verify(animalRepository).findByRg(clinicalRecordDTO.rg());
        verify(clinicalRecordRepository, never()).save(any(ClinicalRecord.class));
    }

    @Test
    void updateRecord_Success() {
        String id = "1";
        Integer newRg = 87654321;
        Date newConsultationDate = new Date();
        String newDescription = "Consulta atualizada";
        ClinicalRecordDTO newDto = new ClinicalRecordDTO(newRg, newConsultationDate, newDescription);

        Animal newAnimal = new Animal();
        newAnimal.setRg(newRg);

        when(clinicalRecordRepository.findById(id)).thenReturn(Optional.of(clinicalRecord));
        when(animalRepository.findByRg(newRg)).thenReturn(Optional.of(newAnimal));
        when(clinicalRecordRepository.save(any(ClinicalRecord.class))).thenReturn(clinicalRecord);

        ArgumentCaptor<ClinicalRecord> recordCaptor = ArgumentCaptor.forClass(ClinicalRecord.class);

        String result = clinicalRecordService.updateRecord(id, newDto);

        assertEquals("Ficha atualizada com sucesso!", result);
        verify(clinicalRecordRepository).findById(id);
        verify(animalRepository).findByRg(newRg);
        verify(clinicalRecordRepository).save(recordCaptor.capture());

        ClinicalRecord updatedRecord = recordCaptor.getValue();
        assertEquals(newAnimal, updatedRecord.getAnimal());
        assertEquals(newConsultationDate, updatedRecord.getConsultationDate());
        assertEquals(newDescription, updatedRecord.getDescription());
    }

    @Test
    void recordClinicalRecordNaoEncontrada_RetornaClinicalRecordNotFoundException() {
        String id = "1";
        when(clinicalRecordRepository.findById(id)).thenReturn(Optional.empty());

        ClinicalRecordNotFoundException exception = assertThrows(ClinicalRecordNotFoundException.class, () -> {
            clinicalRecordService.updateRecord(id, clinicalRecordDTO);
        });

        assertEquals("Ficha clínica não encontrada", exception.getMessage());
        verify(clinicalRecordRepository).findById(id);
        verify(animalRepository, never()).findByRg(anyInt());
        verify(clinicalRecordRepository, never()).save(any(ClinicalRecord.class));
    }

    @Test
    void recordAnimalNaoEncontrado_RetornaAnimalNotFoundException() {
        String id = "1";
        when(clinicalRecordRepository.findById(id)).thenReturn(Optional.of(clinicalRecord));
        when(animalRepository.findByRg(clinicalRecordDTO.rg())).thenReturn(Optional.empty());

        AnimalNotFoundException exception = assertThrows(AnimalNotFoundException.class, () -> {
            clinicalRecordService.updateRecord(id, clinicalRecordDTO);
        });

        assertEquals("Animal não encontrado", exception.getMessage());
        verify(clinicalRecordRepository).findById(id);
        verify(animalRepository).findByRg(clinicalRecordDTO.rg());
        verify(clinicalRecordRepository, never()).save(any(ClinicalRecord.class));
    }

    // Testes para deleteRecord
    @Test
    void deleteRecord_Success() {
        String id = "1";
        when(clinicalRecordRepository.findById(id)).thenReturn(Optional.of(clinicalRecord));

        String result = clinicalRecordService.deleteRecord(id);

        assertEquals("Ficha excluída com sucesso!", result);
        verify(clinicalRecordRepository).findById(id);
        verify(clinicalRecordRepository).delete(clinicalRecord);
    }

    @Test
    void deleteRecordClinicalRecordNaoEncontrado_RetornaClinicalRecordNotFoundException() {
        String id = "1";
        when(clinicalRecordRepository.findById(id)).thenReturn(Optional.empty());

        ClinicalRecordNotFoundException exception = assertThrows(ClinicalRecordNotFoundException.class, () -> {
            clinicalRecordService.deleteRecord(id);
        });

        assertEquals("Ficha clínica não encontrada", exception.getMessage());
        verify(clinicalRecordRepository).findById(id);
        verify(clinicalRecordRepository, never()).delete(any(ClinicalRecord.class));
    }
}