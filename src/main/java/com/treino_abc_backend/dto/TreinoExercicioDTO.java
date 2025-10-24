package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TreinoExercicioDTO {

    @JsonProperty("nome")
    private String nomeExercicio;

    @JsonProperty("grupo_muscular")
    private String grupoMuscular;

    private int series;

    @JsonProperty("rep_min")
    private int repMin;

    @JsonProperty("rep_max")
    private int repMax;

    @JsonProperty("peso_inicial")
    private double pesoInicial;

    private String observacao;

    // Construtor padrão
    public TreinoExercicioDTO() {}

    // Construtor completo
    public TreinoExercicioDTO(String nomeExercicio, String grupoMuscular, int series,
                              int repMin, int repMax, double pesoInicial, String observacao) {
        this.nomeExercicio = nomeExercicio;
        this.grupoMuscular = grupoMuscular;
        this.series = series;
        this.repMin = repMin;
        this.repMax = repMax;
        this.pesoInicial = pesoInicial;
        this.observacao = observacao;
    }

    // Getters e Setters
    public String getNomeExercicio() {
        return nomeExercicio;
    }

    public void setNomeExercicio(String nomeExercicio) {
        this.nomeExercicio = nomeExercicio;
    }

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepMin() {
        return repMin;
    }

    public void setRepMin(int repMin) {
        this.repMin = repMin;
    }

    public int getRepMax() {
        return repMax;
    }

    public void setRepMax(int repMax) {
        this.repMax = repMax;
    }

    public double getPesoInicial() {
        return pesoInicial;
    }

    public void setPesoInicial(double pesoInicial) {
        this.pesoInicial = pesoInicial;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
