package com.project.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.project.model.dto.AnimalDTO;
import com.project.model.entitys.Animal;
import com.project.model.entitys.Usuario;
import com.project.model.exeptions.CpfNotFoundException;
import com.project.model.exeptions.RgNotFoundException;
import com.project.model.exeptions.AnimalNotFoundException;
import com.project.model.repositorys.AnimalRepository;
import com.project.model.repositorys.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnimalService {

    private final AnimalRepository animalRepository;

    private final UserRepository userRepository;

    private final Random random = new Random();

    public AnimalService(AnimalRepository animalRepository, UserRepository userRepository) {
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
    }

    private int generateUniqueRg() {
        return generateRgStream()
                .filter(rg -> !animalRepository.existsByRg(rg))
                .findFirst()
                .orElseThrow();
    }

    private java.util.stream.IntStream generateRgStream() {
        return random.ints(10000000, 99999999).distinct().limit(1000);
    }

    public List<Animal> searchAllAnimals() {
        return animalRepository.findAll();
    }


    public Page<Animal> searchAllAnimalsByUser(String userId, boolean isManager, Pageable pageable) {
        return isManager
                ? animalRepository.findAll(pageable)
                : animalRepository.findByResponsibleId(userId, pageable);
    }

    public Optional<Animal> findByRg(Integer rg) {
        return Optional.ofNullable(animalRepository.findByRg(rg))
                .orElseThrow(() -> new RgNotFoundException("Não existe animal cadastrado com esse RG: " + rg));
    }

    public Animal registerAnimal(AnimalDTO animalDTO) {
        String cpf = animalDTO.responsible();
        Usuario usuario = Optional.ofNullable(userRepository.findByCpf(cpf))
                .orElseThrow(() -> new CpfNotFoundException("CPF não encontrado"));

        Animal animal = Animal.builder()
                .responsible(usuario)
                .name(animalDTO.name())
                .age(animalDTO.age())
                .race(animalDTO.race())
                .specie(animalDTO.specie())
                .photoUrl(animalDTO.photoUrl() != null ? animalDTO.photoUrl() : "")
                .dateRegister(new Date())
                .rg(generateUniqueRg())
                .build();

        return animalRepository.save(animal);

    }

    public String editRegister(Animal animal) {
        Animal existingAnimal = animalRepository.findById(animal.getId())
                .orElseThrow(() -> new AnimalNotFoundException("Animal não encontrado"));

        Optional.ofNullable(animal.getResponsible())
                .ifPresent(newResponsible -> {
                    existingAnimal.getResponsible().setName(newResponsible.getName());
                    userRepository.save(existingAnimal.getResponsible());
                });

        Optional.of(existingAnimal).ifPresent(existing -> {
            existing.setName(animal.getName());
            existing.setSpecie(animal.getSpecie());
            existing.setRace(animal.getRace());
            existing.setAge(animal.getAge());
        });
        animalRepository.saveAndFlush(existingAnimal);
        return "Editado com Sucesso!";
    }

    public void excluir(String id) {
        animalRepository.deleteById(id);
    }

}
