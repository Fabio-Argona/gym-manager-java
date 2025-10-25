package com.treino_abc_backend.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "treino_exercicio")
public class Exercicio {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    // Campo transitório para receber o grupoId via JSON
    @Transient
    private UUID grupoId;

    // Relacionamento com TreinoGrupo para persistência no banco
    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private TreinoGrupo grupo;

    @Column(name = "grupo_muscular", nullable = false)
    private String grupoMuscular;

    @Column(nullable = false)
    private int series;

    @Column(name = "rep_min", nullable = false)
    private int repMin;

    @Column(name = "rep_max", nullable = false)
    private int repMax;

    @Column(name = "peso_inicial", nullable = false)
    private Double pesoInicial;

    @Column(length = 500)
    private String observacao;

    @Column(name = "aluno_id", nullable = false)
    private UUID alunoId;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    // Getters e Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public UUID getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(UUID grupoId) {
        this.grupoId = grupoId;
    }

    public TreinoGrupo getGrupo() {
        return grupo;
    }

    public void setGrupo(TreinoGrupo grupo) {
        this.grupo = grupo;
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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
