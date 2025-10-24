package com.treino_abc_backend.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "treino_exercicio_aluno")
public class TreinoExercicioAluno {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(name = "nome_exercicio", nullable = false)
    private String nomeExercicio;

    @ManyToOne
    @JoinColumn(name = "exercicio_id", referencedColumnName = "id", nullable = false)
    private Exercicio exercicio;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private TreinoGrupo grupo;

    @Column(nullable = false)
    private int ordem;

    @Column(name = "dia_semana", nullable = false)
    private String diaSemana;

    @Column(length = 500)
    private String observacao;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public String getNomeExercicio() {
        return nomeExercicio;
    }

    public void setNomeExercicio(String nomeExercicio) {
        this.nomeExercicio = nomeExercicio;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TreinoGrupo getGrupo() {
        return grupo;
    }

    public void setGrupo(TreinoGrupo grupo) {
        this.grupo = grupo;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
