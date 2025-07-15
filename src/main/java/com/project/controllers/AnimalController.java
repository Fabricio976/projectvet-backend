package com.project.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.project.model.dto.RegisterAnimalDTO;
import com.project.model.entitys.Animal;
import com.project.model.repositorys.AnimalRepository;
import com.project.services.AnimalService;

import jakarta.validation.Valid;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@RestController
@RequestMapping("/projectvet/animal")
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @Autowired
    private AnimalRepository animalRepository;

    private ResponseEntity<Map<String, String>> response(String message) {
        return ResponseEntity.ok(Map.of("message", message));
    }

    @GetMapping("/searchAll")
    public Page<Animal> searchAllAnimals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Authentication auth) {
        String userId = auth.getPrincipal().toString();
        boolean isManager = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("dateRegister").descending());
        return animalService.searchAllAnimalsByUser(userId, isManager, pageable);
    }


    @GetMapping("/search/{id}")
    public ResponseEntity<Animal> searchById(@PathVariable String id) {
        return animalRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/searchByUserCpf/{cpf}")
    public List<Animal> searchByResponsible(@PathVariable String cpf) {
        return animalRepository.findAnimalsByUserCpf(cpf);
    }

    @GetMapping("/animalRg/{rg}")
    public Optional<Animal> getAnimaisByRg(@PathVariable int rg) {
        return animalService.findByRg(rg);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerAnimal(@RequestBody @Valid RegisterAnimalDTO data) {
        String result = animalService.registerAnimal(data);
        return ResponseEntity.ok().body(Map.of("message", result));
    }

    @PatchMapping("/editAnimal/{id}")
    public ResponseEntity<Map<String, String>> editAnimal(@PathVariable String id, @RequestBody Animal animal) {
        Optional.ofNullable(animal)
                .filter(a -> id.equals(a.getId()))
                .orElseThrow(() -> new IllegalArgumentException("IDs devem ser iguais"));

        String result = animalService.editRegister(animal);
        return response(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteAnimal(@PathVariable String id) {
        animalService.excluir(id);
        return response("Animal excluído com sucesso!");
    }


}
