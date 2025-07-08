package com.project.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.project.model.entitys.enums.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.project.model.dto.RegisterAnimalDTO;
import com.project.model.entitys.Animal;
import com.project.model.repositorys.AnimalRepository;
import com.project.services.AnimalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/projectvet/animal")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @Autowired
    private AnimalRepository animalRepository;

    @GetMapping("/searchAll")
    public List<Animal> searchAllAnimals(Authentication auth) {
        String userId = auth.getPrincipal().toString();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        return animalService.searchAllAnimalsByUser(userId, RoleName.valueOf(role));
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
    public Animal getAnimaisByRg(@PathVariable int rg) {
        return animalService.findByRg(rg);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerAnimal(@RequestBody @Valid RegisterAnimalDTO data) {
        String result = animalService.registerAnimal(data);
        return ResponseEntity.ok().body(Map.of("message", result));
    }

    @PutMapping("/editAnimal/{id}")
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

    @GetMapping("/searchBarra")
    public List<Animal> searchAnimals(@RequestParam("query") String query, Authentication auth) {
        String userId = auth.getPrincipal().toString();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        String normalizedQuery = query.trim().toLowerCase();

        return ("MANAGER".equals(role)
                ? animalRepository.findAll()
                : animalRepository.findByResponsibleId(userId))
                .stream()
                .filter(animal -> matchesQuery(animal, normalizedQuery))
                .collect(Collectors.toList());
    }

    private boolean matchesQuery(Animal animal, String query) {
        return Optional.ofNullable(animal)
                .map(a -> Stream.of(
                        String.valueOf(a.getRg()),
                        Optional.ofNullable(a.getName()).orElse("").toLowerCase(),
                        Optional.ofNullable(a.getResponsible())
                                .map(r -> r.getCpf() + r.getEmail())
                                .orElse("")
                                .toLowerCase()
                ).anyMatch(value -> value.contains(query)))
                .orElse(false);
    }

    private ResponseEntity<Map<String, String>> response(String message) {
        return ResponseEntity.ok(Map.of("message", message));
    }
}
