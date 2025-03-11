package com.project.controllers;

import com.project.model.entitys.Animal;
import com.project.model.repositorys.AnimalRepository;
import com.project.services.AnimalService;
import com.project.services.ImagemService;
import com.project.services.details.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/images")
public class ImagemController {

    @Autowired
    private ImagemService imagemService;


    @Autowired
    private AnimalRepository animalRepository;


    private static final String UPLOAD_DIR = "C:/Users/thiag/Desktop/projectvet/imgs/";

    @RestController
    @RequestMapping("/images")
    public class ImageController {

        private static final String UPLOAD_DIR = "C:/Users/thiag/Desktop/projectvet/imgs/";

        @PostMapping("/upload/{animalId}")
        public ResponseEntity<Map<String, String>> uploadImage(
                @PathVariable String animalId,
                @RequestParam("file") MultipartFile file,
                Authentication auth) throws IOException {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nenhum arquivo enviado"));
            }

            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());
            String imageUrl = "/images/view/" + fileName;

            Animal animal = animalRepository.findById(animalId)
                    .orElseThrow(() -> new RuntimeException("Animal não encontrado"));
            animal.setPhotoUrl(imageUrl);
            animalRepository.save(animal);
            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            return ResponseEntity.ok(response);
        }

        @GetMapping("/view/{fileName}")
        public ResponseEntity<byte[]> viewImage(@PathVariable String fileName) throws IOException {
            File file = new File(UPLOAD_DIR + fileName);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(fileContent);
        }
    }

    @DeleteMapping("/remover/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) throws IOException {
        imagemService.remover(id);
        return ResponseEntity.ok().build();
    }

}