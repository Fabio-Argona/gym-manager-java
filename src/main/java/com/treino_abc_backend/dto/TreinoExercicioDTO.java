package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TreinoExercicioDTO {

    @JsonProperty("grupo_id")
    private UUID grupoId;

    @JsonProperty("nome")
    private String nomeExercicio;

    @JsonProperty("grupo_muscular")
    private String grupoMuscular;

    private int series;

    private int repeticoes;

    @JsonProperty("peso_inicial")
    private double pesoInicial;

    private String observacao;

    @JsonProperty("datas_treinadas")
    private List<LocalDate> datasTreinadas;

    // Construtor completo
    public TreinoExercicioDTO(UUID grupoId, String nomeExercicio, String grupoMuscular,
                              int series, int repeticoes, double pesoInicial,
                              String observacao, List<LocalDate> datasTreinadas) {
        this.grupoId = grupoId;
        this.nomeExercicio = nomeExercicio;
        this.grupoMuscular = grupoMuscular;
        this.series = series;
        this.repeticoes = repeticoes;
        this.pesoInicial = pesoInicial;
        this.observacao = observacao;
        this.datasTreinadas = datasTreinadas;
    }

    // Construtor vazio
    public TreinoExercicioDTO() {}

    public UUID getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(UUID grupoId) {
        this.grupoId = grupoId;
    }

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

    public int getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(int repeticoes) {
        this.repeticoes = repeticoes;
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

    public List<LocalDate> getDatasTreinadas() {
        return datasTreinadas;
    }

    public void setDatasTreinadas(List<LocalDate> datasTreinadas) {
        this.datasTreinadas = datasTreinadas;
    }
}
