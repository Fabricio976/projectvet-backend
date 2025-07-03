package com.project.services;

import java.util.Date;
import java.util.List;
import java.util.Random;

import com.project.model.entitys.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.model.dto.RegisterAnimalDTO;
import com.project.model.entitys.Animal;
import com.project.model.entitys.Usuario;
import com.project.model.exeptions.CpfNotFoundException;
import com.project.model.exeptions.RgNotFoundException;
import com.project.model.exeptions.AnimalNotFoundException;
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

    public List<Animal> searchAllAnimalsByUser(String userId, Role role) {
        if ("MANAGER".equals(role)) {
            return animalRepository.findAll();
        } else {
            return animalRepository.findByResponsibleId(userId);
        }
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
     * @param cpf       ID do usuário responsável pelo animal.
     * @param animalDTO Objeto contendo os dados do animal.
     * @return Mensagem de confirmação do registro.
     * @throws CpfNotFoundException Se o usuário responsável não for encontrado.
     */
    public String registerAnimal(String cpf, RegisterAnimalDTO animalDTO) {
        Usuario usuario = userRepository.findByCpf(cpf);
        if (usuario == null) {
            throw new CpfNotFoundException("CPF não encontrado ");
        }
        Animal animal = new Animal();
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

    /**
     * Edita o registro de um animal existente.
     *
     * @param animal Os novos dados a serem atualizados.
     * @return Uma mensagem indicando o sucesso da operação.
     * @throws AnimalNotFoundException Se o animal com o ID fornecido não for encontrado.
     */
    public String editRegister(Animal animal) {
        Animal existingAnimal = animalRepository.findById(animal.getId())
                .orElseThrow(() -> new AnimalNotFoundException("Animal não encontrado"));

        Usuario responsible = existingAnimal.getResponsible();
        existingAnimal.setName(animal.getName());
        existingAnimal.setSpecie(animal.getSpecie());
        existingAnimal.setRace(animal.getRace());
        existingAnimal.setAge(animal.getAge());
        existingAnimal.setServicePet(animal.getServicePet());
        responsible.setName(animal.getResponsible().getName());
        userRepository.save(responsible);

        animalRepository.saveAndFlush(existingAnimal);
        return "Editado com Sucesso!";
    }


    public void excluir(String id) {
        animalRepository.deleteById(id);

    }

    /**
     * Gera um número de 8 dígitos aleatorio para o RG do aniamal
     *
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
