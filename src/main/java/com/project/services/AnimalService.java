package com.project.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.model.dto.RegisterAnimalDTO;
import com.project.model.entitys.Animal;
import com.project.model.entitys.Usuario;
import com.project.model.exeptions.CpfNotFoundException;
import com.project.model.exeptions.RgNotFoundException;
import com.project.model.repositorys.AnimalRepository;
import com.project.model.repositorys.UserRepository;

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

    public Animal findByRg(int rg) {
        Animal animal = animalRepository.findByRg(rg);
        if (animal == null) {
            throw new RgNotFoundException("Não existe animal cadastrado com esse RG: " + rg);
        }
        return animal;
    }
    
    /**
     * Registra um novo animal no sistema.
     *
     * @param id ID do usuário responsável pelo animal.
     * @param animalDTO Objeto contendo os dados do animal.
     * @return Mensagem de confirmação do registro.
     * @throws CpfNotFoundException Se o usuário responsável não for encontrado.
     */
    public String registerAnimal(String id, RegisterAnimalDTO animalDTO) {
        if (userRepository.findById(id).isEmpty()) {
            throw new CpfNotFoundException("Usuário não encontrado!");
        }
        Animal animal = new Animal();
        Usuario usuario = userRepository.findById(id).get();
        animal.setResponsible(usuario);
        animal.setName(animalDTO.name());
        animal.setAge(animalDTO.age());
        animal.setRace(animalDTO.race());
        animal.setSpecie(animalDTO.specie());
        animal.setServicePet(animalDTO.servicePet());
        animal.setDateRegister(new Date());
        animal.setRg(generateUniqueRg());
        animalRepository.save(animal);
        return "Animal Registrado!";
    }

    
    public String editRegister(Animal animal) {
        animalRepository.saveAndFlush(animal);
        return ("Editado com Sucesso!");
    }

    public void excluir(Integer rg) {
        Animal animal = animalRepository.findByRg(rg);
        animalRepository.delete(animal);
    }

    /**
     * Gera um número de 8 dígitos aleatorio para o RG do aniamal
     * @return Rg gerado
     */
    private int generateUniqueRg() {
        int rg;
        do {
            rg = random.nextInt(90000000) + 10000000;
        } while (animalRepository.existsByRg(rg));
        return rg;
    }

}
