package com.project.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.project.model.entitys.enums.Role;
import com.project.model.dto.RegisterAnimalDTO;
import com.project.model.entitys.Animal;
import com.project.model.entitys.Usuario;
import com.project.model.exeptions.CpfNotFoundException;
import com.project.model.exeptions.RgNotFoundException;
import com.project.model.exeptions.AnimalNotFoundException;
import com.project.model.repositorys.AnimalRepository;
import com.project.model.repositorys.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserRepository userRepository;

    private final Random random = new Random();

    public List<Animal> searchAllAnimals() {
        return animalRepository.findAll();
    }

    public List<Animal> searchAllAnimalsByUser(String userId, Role role) {
        return "MANAGER".equals(role.name())
                ? animalRepository.findAll()
                : animalRepository.findByResponsibleId(userId);
    }

    public Animal findByRg(int rg) {
        return Optional.ofNullable(animalRepository.findByRg(rg))
                .orElseThrow(() -> new RgNotFoundException("Não existe animal cadastrado com esse RG: " + rg));
    }

    public String registerAnimal(RegisterAnimalDTO animalDTO) {
        String cpf = animalDTO.responsible();
        Usuario usuario = Optional.ofNullable(userRepository.findByCpf(cpf))
                .orElseThrow(() -> new CpfNotFoundException("CPF não encontrado"));

        Animal animal = new Animal();
        animal.setResponsible(usuario);
        animal.setName(animalDTO.name());
        animal.setAge(animalDTO.age());
        animal.setRace(animalDTO.race());
        animal.setSpecie(animalDTO.specie());
        animal.setPhotoUrl(animalDTO.photoUrl() != null ? animalDTO.photoUrl() : "");
        animal.setServicePet(animalDTO.servicePet());
        animal.setDateRegister(new Date());
        animal.setRg(generateUniqueRg());

        animalRepository.save(animal);
        return "Animal Registrado!";
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
            existing.setServicePet(animal.getServicePet());
        });

        animalRepository.saveAndFlush(existingAnimal);
        return "Editado com Sucesso!";
    }

    public void excluir(String id) {
        animalRepository.deleteById(id);
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
}
