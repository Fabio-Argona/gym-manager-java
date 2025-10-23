package com.treino_abc_backend.dto;

import java.util.UUID;

public class TreinoGrupoDTO {

    private UUID id;
    private UUID alunoId;
    private String nome;

    // Construtor padrão
    public TreinoGrupoDTO() {}

    // Construtor completo
    public TreinoGrupoDTO(UUID id, UUID alunoId, String nome) {
        this.id = id;
        this.alunoId = alunoId;
        this.nome = nome;
    }

    // Getters e Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(UUID alunoId) {
        this.alunoId = alunoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
