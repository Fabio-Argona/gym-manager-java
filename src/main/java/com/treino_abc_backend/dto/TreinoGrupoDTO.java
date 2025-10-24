package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public class TreinoGrupoDTO {

    private UUID id;

    @JsonProperty("aluno_id")
    private UUID alunoId;

    private String nome;

    private List<TreinoExercicioDTO> exercicios;

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

    public List<TreinoExercicioDTO> getExercicios() {
        return exercicios;
    }

    public void setExercicios(List<TreinoExercicioDTO> exercicios) {
        this.exercicios = exercicios;
    }
}
