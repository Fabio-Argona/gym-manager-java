package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FichaTreinoDTO {

    @JsonProperty("nome")
    private String exercicio;

    @JsonProperty("grupo_muscular")
    private String grupoMuscular;

    private Integer series;

    @JsonProperty("repeticoes")
    private Integer repeticoes;

    @JsonProperty("peso_inicial")
    private Double pesoInicial;

    private String observacao;

    public FichaTreinoDTO() {}

    public FichaTreinoDTO(String exercicio, String grupoMuscular, int series,
                          int repeticoes, Double pesoInicial, String observacao) {
        this.exercicio = exercicio;
        this.grupoMuscular = grupoMuscular;
        this.series = series;
        this.repeticoes = repeticoes;
        this.pesoInicial = pesoInicial;
        this.observacao = observacao;
    }


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

    public Integer repeticoes() {
        return repeticoes;
    }

    public void repeticoes(Integer repeticoes) {
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
}
