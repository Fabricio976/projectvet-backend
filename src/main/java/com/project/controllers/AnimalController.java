package com.project.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.project.model.dto.AnimalResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.project.model.dto.AnimalDTO;
import com.project.model.entitys.Animal;
import com.project.model.repositorys.AnimalRepository;
import com.project.services.AnimalService;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@RestController
@RequestMapping("/projectvet/animals")
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class AnimalController {

    private final AnimalService animalService;

    private final AnimalRepository animalRepository;

    public AnimalController(AnimalService animalService, AnimalRepository animalRepository) {
        this.animalService = animalService;
        this.animalRepository = animalRepository;
    }

    private ResponseEntity<Map<String, String>> response(String message) {
        return ResponseEntity.ok(Map.of("message", message));
    }

    // GET /projectvet/animals?page=0&size=5
    @GetMapping
    public Page<Animal> getAllAnimals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Authentication auth) {
        String userId = auth.getPrincipal().toString();
        boolean isManager = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("dateRegister").descending());
        return animalService.searchAllAnimalsByUser(userId, isManager, pageable);
    }

    // GET /projectvet/animals/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Animal> getById(@PathVariable String id) {
        return animalRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /projectvet/animals/by-user/{cpf}
    @GetMapping("/by-user/{cpf}")
    public List<Animal> getByUserCpf(@PathVariable String cpf) {
        return animalRepository.findAnimalsByUserCpf(cpf);
    }

    // GET /projectvet/animals/by-rg/{rg}
    @GetMapping("/by-rg/{rg}")
    public Optional<Animal> getByRg(@PathVariable int rg) {return animalService.findByRg(rg);
    }

    // POST /projectvet/animals
    @PostMapping
    public ResponseEntity<?> createAnimal(@RequestBody AnimalDTO data) {
        Animal result = animalService.registerAnimal(data);
        return ResponseEntity.ok().body(Map.of("message", "Animal registrado com sucesso!",
                "animal", new AnimalResponseDTO(result.getId(), result.getRg(), result.getName())));
    }

    @PatchMapping("/editAnimal/{id}")
    public ResponseEntity<Map<String, String>> editAnimal(@PathVariable String id, @RequestBody Animal animal) {
        animal.setId(id);
        Optional.of(animal)
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
