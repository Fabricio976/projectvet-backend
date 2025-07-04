package com.project.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.project.model.entitys.Imagem;
import com.project.model.repositorys.ImagemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImagemService {

    @Value("${contato.disco.raiz}")
    private String raiz;

    @Value("${contato.disco.diretorio-imagens}")
    private String diretorioImagens;

    @Autowired
    private ImagemRepository imagemRepository;

    public String salvar(MultipartFile arquivo, String nomeImagem) {
        Path diretorioImagemPath = Paths.get(raiz, diretorioImagens);
        Path arquivoPath = diretorioImagemPath.resolve(arquivo.getOriginalFilename());

        String nomeBase = nomeImagem;
        String extensao = "";

        if (nomeImagem.contains(".")) {
            int lastDot = nomeImagem.lastIndexOf(".");
            nomeBase = nomeImagem.substring(0, lastDot);
            extensao = nomeImagem.substring(lastDot);
        }

        int cont = 1;
        while (Files.exists(arquivoPath)) {
            nomeImagem = nomeBase + "_" + cont++ + extensao;
            arquivoPath = diretorioImagemPath.resolve(nomeImagem);
        }

        try {
            Files.createDirectories(diretorioImagemPath);
            arquivo.transferTo(arquivoPath.toFile());
            return "/images/view/" + nomeImagem;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar a imagem", e);
        }
    }

    public void remover(Long id) {
        imagemRepository.findById(id).ifPresentOrElse(img -> {
            try {
                Path path = Paths.get(new File(img.getCaminho()).getAbsolutePath());
                Files.deleteIfExists(path);
                imagemRepository.delete(img);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao deletar a imagem do sistema de arquivos", e);
            }
        }, () -> {
            throw new RuntimeException("Imagem não encontrada com ID: " + id);
        });
    }
}
