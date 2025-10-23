package com.treino_abc_backend.dto;

import java.util.UUID;

public class TreinoDTO {

    private UUID id;
    private UUID grupoId;
    private UUID alunoId;
    private UUID exercicioId;
    private String nomeExercicio;
    private Integer series;
    private Integer repMin;
    private Integer repMax;
    private Double pesoInicial;
    private String diaSemana;
    private Integer ordem;
    private String observacao;

    public TreinoDTO() {
        // Construtor vazio para frameworks
    }

    public TreinoDTO(UUID id, UUID grupoId, UUID alunoId, UUID exercicioId, String nomeExercicio,
                     Integer series, Integer repMin, Integer repMax, Double pesoInicial,
                     String diaSemana, Integer ordem, String observacao) {
        this.id = id;
        this.grupoId = grupoId;
        this.alunoId = alunoId;
        this.exercicioId = exercicioId;
        this.nomeExercicio = nomeExercicio;
        this.series = series;
        this.repMin = repMin;
        this.repMax = repMax;
        this.pesoInicial = pesoInicial;
        this.diaSemana = diaSemana;
        this.ordem = ordem;
        this.observacao = observacao;
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

    public UUID getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(UUID alunoId) {
        this.alunoId = alunoId;
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

    public Integer getRepMin() {
        return repMin;
    }

    public void setRepMin(Integer repMin) {
        this.repMin = repMin;
    }

    public Integer getRepMax() {
        return repMax;
    }

    public void setRepMax(Integer repMax) {
        this.repMax = repMax;
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
}
