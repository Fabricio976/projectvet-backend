package com.project.model.entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "imagens")
public class Imagem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nome;

    private String caminho;

    @Transient
    private byte [] arquivo;

    public Imagem(String nome, String caminho, byte[] arquivo) {
        setNome(nome);
        setCaminho(caminho);

    }
}
