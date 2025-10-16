package com.treino_abc_backend.dto;

public class FichaTreinoDTO {
    private String exercicio;
    private String grupoMuscular;
    private Integer series;
    private Integer repMin;
    private Integer repMax;
    private Double pesoInicial;
    private String observacao;

    public String getExercicio() {
        return exercicio;
    }

    public void setExercicio(String exercicio) {
        this.exercicio = exercicio;
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

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }


    // Construtor
    public FichaTreinoDTO(String exercicio, String grupoMuscular, Integer series, Integer repMin, Integer repMax, Double pesoInicial, String observacao) {
        this.exercicio = exercicio;
        this.grupoMuscular = grupoMuscular;
        this.series = series;
        this.repMin = repMin;
        this.repMax = repMax;
        this.pesoInicial = pesoInicial;
        this.observacao = observacao;
    }


}
