package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class TreinoInputDTO {

    @NotNull
    @JsonProperty("aluno_id")
    private UUID alunoId;

    @NotNull
    @JsonProperty("grupo_id")
    private UUID grupoId;

    @NotNull
    @JsonProperty("exercicio_id")
    private UUID exercicioId;

    @NotNull
    @Size(min = 3, max = 20)
    @JsonProperty("dia_semana")
    private String diaSemana;

    private Integer ordem;

    @Size(max = 500)
    private String observacao;

    // Getters e setters


    public @NotNull UUID getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(@NotNull UUID alunoId) {
        this.alunoId = alunoId;
    }

    public @NotNull UUID getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(@NotNull UUID grupoId) {
        this.grupoId = grupoId;
    }

    public @NotNull UUID getExercicioId() {
        return exercicioId;
    }

    public void setExercicioId(@NotNull UUID exercicioId) {
        this.exercicioId = exercicioId;
    }

    public @NotNull @Size(min = 3, max = 20) String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(@NotNull @Size(min = 3, max = 20) String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public @Size(max = 500) String getObservacao() {
        return observacao;
    }

    public void setObservacao(@Size(max = 500) String observacao) {
        this.observacao = observacao;
    }
}
