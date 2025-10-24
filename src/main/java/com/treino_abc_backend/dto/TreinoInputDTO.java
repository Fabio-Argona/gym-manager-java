package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class TreinoInputDTO {

    @JsonProperty("aluno_id")
    private UUID alunoId;

    @JsonProperty("grupo_id")
    private UUID grupoId;

    @JsonProperty("exercicio_id")
    private UUID exercicioId;

    @JsonProperty("dia_semana")
    private String diaSemana;

    private Integer ordem;

    private String observacao;

    // Construtor padrão
    public TreinoInputDTO() {}

    // Construtor completo
    public TreinoInputDTO(UUID alunoId, UUID grupoId, UUID exercicioId, String diaSemana, Integer ordem, String observacao) {
        this.alunoId = alunoId;
        this.grupoId = grupoId;
        this.exercicioId = exercicioId;
        this.diaSemana = diaSemana;
        this.ordem = ordem;
        this.observacao = observacao;
    }

    // Getters e Setters
    public UUID getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(UUID alunoId) {
        this.alunoId = alunoId;
    }

    public UUID getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(UUID grupoId) {
        this.grupoId = grupoId;
    }

    public UUID getExercicioId() {
        return exercicioId;
    }

    public void setExercicioId(UUID exercicioId) {
        this.exercicioId = exercicioId;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
