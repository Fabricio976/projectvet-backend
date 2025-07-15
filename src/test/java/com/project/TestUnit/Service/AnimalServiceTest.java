package com.project.TestUnit.Service;

import com.project.model.dto.RegisterAnimalDTO;
import com.project.model.entitys.Animal;
import com.project.model.entitys.Usuario;
import com.project.model.exeptions.AnimalNotFoundException;
import com.project.model.exeptions.CpfNotFoundException;
import com.project.model.exeptions.RgNotFoundException;
import com.project.model.repositorys.AnimalRepository;
import com.project.model.repositorys.UserRepository;
import com.project.services.AnimalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AnimalService animalService;

    private Animal animal;
    private Usuario usuario;
    private RegisterAnimalDTO animalDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId("user1");
        usuario.setCpf("123.456.789-01");
        usuario.setName("Ze do Pão");

        animal = Animal.builder()
                .id("animal1")
                .rg(12345678)
                .name("Rex")
                .age(5)
                .race("Labrador")
                .specie("Dog")
                .photoUrl("")
                .responsible(usuario)
                .dateRegister(new Date())
                .build();

        animalDTO = new RegisterAnimalDTO(
                "Rex", 5, "Labrador", "Dog", "123.456.789-01", "");

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void searchAllAnimals_RetornaTodosAnimals() {
        List<Animal> animals = List.of(animal, new Animal());
        when(animalRepository.findAll()).thenReturn(animals);

        List<Animal> result = animalService.searchAllAnimals();

        assertEquals(animals, result);
        verify(animalRepository).findAll();
    }

    @Test
    void searchAllAnimalsByUser_RetornaTodosAnimaisSeForManager() {
        Page<Animal> page = new PageImpl<>(List.of(animal));
        when(animalRepository.findAll(pageable)).thenReturn(page);

        Page<Animal> result = animalService.searchAllAnimalsByUser("user1", true, pageable);

        assertEquals(page, result);
        verify(animalRepository).findAll(pageable);
        verify(animalRepository, never()).findByResponsibleId(anyString(), any(Pageable.class));
    }

    @Test
    void searchAllAnimalsByUser_NaoRetornaTodosOsAnimaisSeNaoForManager() {
        Page<Animal> page = new PageImpl<>(List.of(animal));
        when(animalRepository.findByResponsibleId("user1", pageable)).thenReturn(page);

        Page<Animal> result = animalService.searchAllAnimalsByUser("user1", false, pageable);

        assertEquals(page, result);
        verify(animalRepository).findByResponsibleId("user1", pageable);
        verify(animalRepository, never()).findAll(pageable);
    }

    @Test
    void findByRg_AnimailEncontrado_RetornaPeloRg() {
        when(animalRepository.findByRg(12345678)).thenReturn(Optional.ofNullable(animal));

        Optional<Animal> result = animalService.findByRg(12345678);

        assertTrue(result.isPresent());
        assertEquals(animal, result.get());
        verify(animalRepository).findByRg(12345678);
    }

    @Test
    void findByRg_AnimailNaoEncontrado_RetornaPeloRg() {
        when(animalRepository.findByRg(12345678)).thenReturn(null);

        RgNotFoundException exception = assertThrows(RgNotFoundException.class, () -> {
            animalService.findByRg(12345678);
        });

        assertEquals("Não existe animal cadastrado com esse RG: 12345678", exception.getMessage());
        verify(animalRepository).findByRg(12345678);
    }

    @Test
    void registerAnimal_SucessoEmRegistarAnimal() {
        when(userRepository.findByCpf("123.456.789-01")).thenReturn(usuario);
        when(animalRepository.existsByRg(anyInt())).thenReturn(false);
        when(animalRepository.save(any(Animal.class))).thenReturn(animal);

        String result = animalService.registerAnimal(animalDTO);

        assertEquals("Animal Registrado!", result);

        ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
        verify(animalRepository).save(captor.capture());
        Animal savedAnimal = captor.getValue();

        assertEquals("Rex", savedAnimal.getName());
        assertEquals(5, savedAnimal.getAge());
        assertEquals("Labrador", savedAnimal.getRace());
        assertEquals("Dog", savedAnimal.getSpecie());
        assertEquals("", savedAnimal.getPhotoUrl());
        assertEquals(usuario, savedAnimal.getResponsible());
        assertNotNull(savedAnimal.getRg());
        assertNotNull(savedAnimal.getDateRegister());

        verify(userRepository).findByCpf("123.456.789-01");
        verify(animalRepository).existsByRg(anyInt());
    }

    @Test
    void registerAnimal_UsuarioNaoEncontrado_RetornaCpfNotFoundException() {
        when(userRepository.findByCpf("123.456.789-01")).thenReturn(null);
        CpfNotFoundException exception = assertThrows(CpfNotFoundException.class, () -> {
            animalService.registerAnimal(animalDTO);
        });

        assertEquals("CPF não encontrado", exception.getMessage());
        verify(userRepository).findByCpf("123.456.789-01");
        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    void editRegister_SucessoAoEditarAnimal() {

        Animal updatedAnimal = Animal.builder()
                .id("animal1")
                .name("Max")
                .age(6)
                .race("Golden")
                .specie("Dog")
                .photoUrl("")
                .responsible(usuario)
                .build();

        when(animalRepository.findById("animal1")).thenReturn(Optional.of(animal));
        when(animalRepository.saveAndFlush(any(Animal.class))).thenReturn(animal);
        when(userRepository.save(any(Usuario.class))).thenReturn(usuario);

        String result = animalService.editRegister(updatedAnimal);

        assertEquals("Editado com Sucesso!", result);

        ArgumentCaptor<Animal> animalCaptor = ArgumentCaptor.forClass(Animal.class);
        verify(animalRepository).saveAndFlush(animalCaptor.capture());

        Animal savedAnimal = animalCaptor.getValue();
        assertEquals("Max", savedAnimal.getName());
        assertEquals(6, savedAnimal.getAge());
        assertEquals("Golden", savedAnimal.getRace());
        assertEquals("Dog", savedAnimal.getSpecie());
        assertEquals("", savedAnimal.getPhotoUrl());

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(userRepository).save(usuarioCaptor.capture());
        assertEquals("Ze do Pão", usuarioCaptor.getValue().getName());

        verify(animalRepository).findById("animal1");
    }

    @Test
    void editRegister_AnimalNaoEncontrado_RetornaAnimalNotFoundException() {
        when(animalRepository.findById("animal1")).thenReturn(Optional.empty());

        AnimalNotFoundException exception = assertThrows(AnimalNotFoundException.class, () -> {
            animalService.editRegister(animal);
        });

        assertEquals("Animal não encontrado", exception.getMessage());
        verify(animalRepository).findById("animal1");
        verify(animalRepository, never()).saveAndFlush(any(Animal.class));
        verify(userRepository, never()).save(any(Usuario.class));
    }

    @Test
    void excluir_ShouldDeleteAnimalById() {
        animalService.excluir("animal1");

        verify(animalRepository).deleteById("animal1");
    }
}