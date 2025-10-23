package com.treino_abc_backend.dto;

import java.util.List;
import java.util.UUID;

public class TreinoGrupoDTO {
    private UUID id;
    private String nome;
    private List<TreinoExercicioDTO> exercicios;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    // Getters e Setters
}
