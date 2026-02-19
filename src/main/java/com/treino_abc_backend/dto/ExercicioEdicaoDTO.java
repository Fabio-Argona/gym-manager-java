package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class ExercicioEdicaoDTO {

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("grupo_id")
    private UUID grupoId;

    @JsonProperty("grupo_muscular")
    private String grupoMuscular;

    @JsonProperty("series")
    private int series;

    @JsonProperty("repeticoes")
    private int repeticoes;

    @JsonProperty("peso_inicial")
    private Double pesoInicial;

    @JsonProperty("observacao")
    private String observacao;

    @JsonProperty("ativo")
    private boolean ativo;

    // ===================== Construtores =====================
    public ExercicioEdicaoDTO() {
        // valores padrão
        this.repeticoes = 0;
        this.ativo = true;
    }

    public ExercicioEdicaoDTO(String nome, UUID grupoId, String grupoMuscular,
                              int series, int repeticoes, Double pesoInicial,
                              String observacao, boolean ativo) {
        this.nome = nome;
        this.grupoId = grupoId;
        this.grupoMuscular = grupoMuscular;
        this.series = series;
        this.repeticoes = repeticoes;
        this.pesoInicial = pesoInicial;
        this.observacao = observacao;
        this.ativo = ativo;
    }

    // ===================== Getters e Setters =====================
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public UUID getGrupoId() { return grupoId; }
    public void setGrupoId(UUID grupoId) { this.grupoId = grupoId; }

    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }

    public int getSeries() { return series; }
    public void setSeries(int series) { this.series = series; }

    public int getRepeticoes() { return repeticoes; }
    public void setRepeticoes(int repeticoes) { this.repeticoes = repeticoes; }

    public Double getPesoInicial() { return pesoInicial; }
    public void setPesoInicial(Double pesoInicial) { this.pesoInicial = pesoInicial; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return "ExercicioEdicaoDTO{" +
                "nome='" + nome + '\'' +
                ", grupoId=" + grupoId +
                ", grupoMuscular='" + grupoMuscular + '\'' +
                ", series=" + series +
                ", repeticoes=" + repeticoes +
                ", pesoInicial=" + pesoInicial +
                ", observacao='" + observacao + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}
