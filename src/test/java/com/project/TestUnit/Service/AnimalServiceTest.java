package com.project.TestUnit.Service;

import com.project.model.dto.RegisterAnimalDTO;
import com.project.model.entitys.Animal;
import com.project.model.entitys.Usuario;
import com.project.model.entitys.enums.ServicePet;
import com.project.model.exeptions.AnimalNotFoundException;
import com.project.model.exeptions.CpfNotFoundException;
import com.project.model.exeptions.RgNotFoundException;
import com.project.model.repositorys.AnimalRepository;
import com.project.model.repositorys.UserRepository;
import com.project.services.AnimalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.util.*;

        import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AnimalService animalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchAllAnimals() {
        List<Animal> animals = List.of(new Animal(), new Animal());
        when(animalRepository.findAll()).thenReturn(animals);

        List<Animal> result = animalService.searchAllAnimals();
        assertEquals(2, result.size());
    }

    @Test
    void testSearchAllAnimalsByUser_WhenManager() {
        List<Animal> animals = List.of(new Animal());
        when(animalRepository.findAll()).thenReturn(animals);

        List<Animal> result = animalService.searchAllAnimalsByUser("user1", true, Pageable.unpaged());
        assertEquals(1, result.size());
    }

    @Test
    void testFindByRgFound() {
        Animal animal = new Animal();
        when(animalRepository.findByRg(123456)).thenReturn(Optional.of(animal));

        Optional<Animal> result = animalService.findByRg(123456);
        assertNotNull(result);
    }

    @Test
    void testFindByRgNotFound() {
        when(animalRepository.findByRg(112345)).thenReturn(null);

        assertThrows(RgNotFoundException.class, () -> animalService.findByRg(112345));
    }

    @Test
    void testRegisterAnimalSuccess() {
        RegisterAnimalDTO dto = new RegisterAnimalDTO("Ximbinha", 5, "Raça", "Gato", "123.456.789-00", null, ServicePet.VETERINARY);
        Usuario usuario = new Usuario();
        when(userRepository.findByCpf("123.456.789-00")).thenReturn(usuario);
        when(animalRepository.existsByRg(anyInt())).thenReturn(false);

        String result = animalService.registerAnimal(dto);

        assertEquals("Animal Registrado!", result);
        verify(animalRepository).save(any(Animal.class));
    }

    @Test
    void testRegisterAnimalCpfNotFound() {
        RegisterAnimalDTO dto = new RegisterAnimalDTO("Juares", 5, "Raça", "Cachorro", "000.000.000-00", "PETSHOP", ServicePet.PETSHOP);
        when(userRepository.findByCpf("000.000.000-00")).thenReturn(null);

        assertThrows(CpfNotFoundException.class, () -> animalService.registerAnimal(dto));
    }

    @Test
    void testEditRegisterSuccess() {
        Animal existing = new Animal();
        existing.setId("1");
        existing.setResponsible(new Usuario());

        Animal toUpdate = new Animal();
        toUpdate.setId("1");
        Usuario newResponsible = new Usuario();
        newResponsible.setName("New Name");
        toUpdate.setResponsible(newResponsible);

        when(animalRepository.findById("1")).thenReturn(Optional.of(existing));

        String result = animalService.editRegister(toUpdate);

        assertEquals("Editado com Sucesso!", result);
        verify(animalRepository).saveAndFlush(existing);
        verify(userRepository).save(existing.getResponsible());
    }

    @Test
    void testEditRegisterNotFound() {
        Animal toUpdate = new Animal();
        toUpdate.setId("444");
        when(animalRepository.findById("444")).thenReturn(Optional.empty());

        assertThrows(AnimalNotFoundException.class, () -> animalService.editRegister(toUpdate));
    }

    @Test
    void testExcluir() {
        doNothing().when(animalRepository).deleteById("1");

        animalService.excluir("1");

        verify(animalRepository).deleteById("1");
    }
}
