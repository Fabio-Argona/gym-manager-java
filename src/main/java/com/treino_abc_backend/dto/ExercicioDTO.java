package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.treino_abc_backend.enums.StatusTreino;

import java.time.LocalDateTime;
import java.util.UUID;

public class ExercicioDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("grupo_id")
    private UUID grupoId;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("grupo_muscular")
    private String grupoMuscular;

    @JsonProperty("series")
    private Integer series;

    @JsonProperty("repeticoes")
    private Integer repeticoes;

    @JsonProperty("peso_inicial")
    private Double pesoInicial;

    @JsonProperty("observacao")
    private String observacao;

    @JsonProperty("aluno_id")
    private UUID alunoId;

    @JsonProperty("ativo")
    private boolean ativo;

    @JsonProperty("data_criacao")
    private LocalDateTime dataCriacao;

    @JsonProperty("status")
    private StatusTreino status;

    @JsonProperty("treino_exercicio_aluno_id")
    private UUID treinoExercicioAlunoId;

    public ExercicioDTO() {
        this.repeticoes = 0;
        this.ativo = true;
    }

    public ExercicioDTO(UUID id, UUID grupoId, String nome, String grupoMuscular,
            Integer series, Integer repeticoes, Double pesoInicial,
            String observacao, UUID alunoId, boolean ativo,
            LocalDateTime dataCriacao, StatusTreino status) {
        this.id = id;
        this.grupoId = grupoId;
        this.nome = nome;
        this.grupoMuscular = grupoMuscular;
        this.series = series;
        this.repeticoes = repeticoes;
        this.pesoInicial = pesoInicial;
        this.observacao = observacao;
        this.alunoId = alunoId;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(UUID grupoId) {
        this.grupoId = grupoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public Integer getSeries() {
        return series;
    }

    public void setSeries(Integer series) {
        this.series = series;
    }

    public Integer getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(Integer repeticoes) {
        this.repeticoes = repeticoes;
    }

    public Double getPesoInicial() {
        return pesoInicial;
    }

    public void setPesoInicial(Double pesoInicial) {
        this.pesoInicial = pesoInicial;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public UUID getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(UUID alunoId) {
        this.alunoId = alunoId;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public StatusTreino getStatus() {
        return status;
    }

    public void setStatus(StatusTreino status) {
        this.status = status;
    }

    public UUID getTreinoExercicioAlunoId() {
        return treinoExercicioAlunoId;
    }

    public void setTreinoExercicioAlunoId(UUID treinoExercicioAlunoId) {
        this.treinoExercicioAlunoId = treinoExercicioAlunoId;
    }

    @Override
    public String toString() {
        return "ExercicioDTO{" +
                "id=" + id +
                ", grupoId=" + grupoId +
                ", nome='" + nome + '\'' +
                ", grupoMuscular='" + grupoMuscular + '\'' +
                ", series=" + series +
                ", repeticoes=" + repeticoes +
                ", pesoInicial=" + pesoInicial +
                ", observacao='" + observacao + '\'' +
                ", alunoId=" + alunoId +
                ", ativo=" + ativo +
                ", dataCriacao=" + dataCriacao +
                ", status=" + status +
                '}';
    }
}
