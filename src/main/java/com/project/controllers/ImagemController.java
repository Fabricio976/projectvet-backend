package com.project.controllers;


import com.project.model.entitys.Imagem;
import com.project.services.ImagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/projectvet/imagem")
public class ImagemController {

    @Autowired
    private ImagemService imagemService;


    @GetMapping("/findById/{id}")
    public ResponseEntity<Imagem> encontrarImagemPorId(@PathVariable Long idImg) {
        Imagem imagem = imagemService.findPorId(idImg);
        if (imagem != null) {
            return ResponseEntity.ok(imagem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/upImagem")
    public ResponseEntity<Imagem> upImagemPost(@RequestParam("file") MultipartFile file,
                                               @ModelAttribute Imagem imagem) throws IOException {
        return ResponseEntity.ok().body(imagemService.upImagem(file));
    }

    @DeleteMapping("/remover/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) throws IOException {
        imagemService.remover(id);
        return ResponseEntity.ok().build();
    }

}