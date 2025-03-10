package com.project.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.model.dto.RegisterAnimalDTO;
import com.project.model.entitys.Animal;
import com.project.model.repositorys.AnimalRepository;
import com.project.services.AnimalService;

import jakarta.validation.Valid;
/**
 * Controlador REST para gerenciar operações relacionadas a animais.
 * Esta classe fornece endpoints para buscar, registrar, editar e excluir animais.
 */
@RestController
@RequestMapping("/projectvet/animal")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @Autowired
    private AnimalRepository animalRepository;


    /**
     * Busca todos os animais registrados.
     *
     * @param auth O objeto de autenticação do usuário.
     * @return Uma lista de todos os animais.
     */
    @GetMapping("/searchAll")
    public List<Animal> searchAllanimals(Authentication auth) {
        String userId = auth.getPrincipal().toString();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        if (role.equals("MANAGER")) {
            // Funcionários veem todos os animais
            return animalService.searchAllAnimals();
        } else {
            // Clientes veem apenas seus animais
            return animalService.searchAllAnimalsByUser(String.valueOf(userId));
        }

    }

    @GetMapping("/search/{id}")
    public Optional<Animal> searchById(@PathVariable String id) {
        return animalRepository.findById(id);
    }

    /**
     * Busca animais registrados pelo CPF do responsável.
     *
     * @param cpf O CPF do responsável pelos animais.
     * @return Uma lista de animais associados ao CPF fornecido.
     */
    @GetMapping("/searchByUserCpf/{cpf}")
    public List<Animal> searchByResponsible(@PathVariable String cpf) {
        return animalRepository.findAnimalsByUserCpf(cpf);
    }

    /**
     * Busca um animal pelo RG.
     *
     * @param rg O RG do animal a ser buscado.
     * @return O animal correspondente ao RG fornecido.
     */
    @GetMapping("/animalRg/{rg}")
    public Animal getAnimaisByUserCpf(@PathVariable int rg) {
        return animalService.findByRg(rg);
    }

    /**
     * Registra um novo animal.
     *
     * @param dataAnimal Os dados do animal a ser registrado.
     * @param auth O objeto de autenticação do usuário.
     */
    @PostMapping("/register")
    public String registerAnimal(@RequestBody @Valid RegisterAnimalDTO dataAnimal, Authentication auth) {
        return animalService.registerAnimal(auth.getPrincipal().toString(), dataAnimal);
    }

    /**
     * Edita o registro de um animal existente.
     *
     * @param id O animal com os dados atualizados.
     * @return Uma resposta com o status da operação.
     */
    @PutMapping("/editAnimal/{id}")
    public ResponseEntity<Map<String, String>> editAnimal(@PathVariable String id, @RequestBody Animal animal) {
        if (!id.equals(animal.getId())) {
            throw new IllegalArgumentException("IDs devem ser iguais");
        }
        String result = animalService.editRegister(animal);
        Map<String, String> response = new HashMap<>();
        response.put("message", result);
        return ResponseEntity.ok(response);
    }
    /**
     * Exclui um animal pelo RG.
     *
     * @param id O RG do animal a ser excluído.
     * @return Uma resposta vazia com o status da operação.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteAnimal(@PathVariable String id) {
        animalService.excluir(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Animal excluído com sucesso!");
        return ResponseEntity.ok(response);
    }
}
