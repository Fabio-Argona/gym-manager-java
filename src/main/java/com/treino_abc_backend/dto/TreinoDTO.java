package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

public class TreinoDTO {

    private UUID id;

    @JsonProperty("aluno_id")
    private UUID alunoId;

    @JsonProperty("grupo_id")
    private UUID grupoId;

    @JsonProperty("exercicio_id")
    private UUID exercicioId;

    @JsonProperty("nome")
    private String nomeExercicio;

    private Integer series;

    @JsonProperty("repeticoes")
    private Integer repeticoes;


    @JsonProperty("peso_inicial")
    private Double pesoInicial;

    @JsonProperty("dia_semana")
    private String diaSemana;

    private Integer ordem;

    private String observacao;

    @JsonProperty("datas_treinadas")
    private List<LocalDate> datasTreinadas;

    // Construtores, getters e setters

    public TreinoDTO(UUID id, UUID alunoId, UUID grupoId, UUID exercicioId, String nomeExercicio, Integer series, Integer repMin, Integer repMax, Double pesoInicial, String diaSemana, Integer ordem, String observacao, List<LocalDate> datasTreinadas) {
        this.id = id;
        this.alunoId = alunoId;
        this.grupoId = grupoId;
        this.exercicioId = exercicioId;
        this.nomeExercicio = nomeExercicio;
        this.series = series;
        this.repeticoes = repMin;
        this.pesoInicial = pesoInicial;
        this.diaSemana = diaSemana;
        this.ordem = ordem;
        this.observacao = observacao;
        this.datasTreinadas = datasTreinadas;
    }

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

    public String getNomeExercicio() {
        return nomeExercicio;
    }

    public void setNomeExercicio(String nomeExercicio) {
        this.nomeExercicio = nomeExercicio;
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

    public List<LocalDate> getDatasTreinadas() {
        return datasTreinadas;
    }

    public void setDatasTreinadas(List<LocalDate> datasTreinadas) {
        this.datasTreinadas = datasTreinadas;
    }
}
