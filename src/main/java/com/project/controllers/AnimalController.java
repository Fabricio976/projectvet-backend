package com.project.controllers;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
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
     * @throws Exception Se ocorrer um erro durante a busca.
     */
    @GetMapping("/searchAll")
    public List<Animal> searchAllanimals(Authentication auth) throws Exception {
        return animalService.searchAllAnimals();
    }

    /**
     * Busca animais registrados pelo CPF do responsável.
     *
     * @param cpf O CPF do responsável pelos animais.
     * @return Uma lista de animais associados ao CPF fornecido.
     * @throws Exception Se ocorrer um erro durante a busca.
     */
    @GetMapping("/searchByUserCpf/{cpf}")
    public List<Animal> searchByResponsible(@PathVariable String cpf) throws Exception {
        return animalRepository.findAnimalsByUserCpf(cpf);
    }

    /**
     * Busca um animal pelo RG.
     *
     * @param auth O objeto de autenticação do usuário.
     * @param rg O RG do animal a ser buscado.
     * @return O animal correspondente ao RG fornecido.
     */
    @GetMapping("/animalRg/{rg}")
    public Animal getAnimaisByUserCpf(Authentication auth, @PathVariable int rg) {
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
     * @param idAnimal O animal com os dados atualizados.
     * @return Uma resposta com o status da operação.
     */
    @PutMapping("/edit")
    public ResponseEntity<?> alterar(@RequestParam("idanimal") Animal idAnimal) {
        return ResponseEntity.ok().body(animalService.editRegister(idAnimal));
    }

    /**
     * Exclui um animal pelo RG.
     *
     * @param rg O RG do animal a ser excluído.
     * @return Uma resposta vazia com o status da operação.
     */
    @DeleteMapping("/excluir/{rg}")
    public ResponseEntity<Void> excluir(@PathVariable("rg") Integer rg) {
        animalService.excluir(rg);
        return ResponseEntity.ok().build();
    }
}