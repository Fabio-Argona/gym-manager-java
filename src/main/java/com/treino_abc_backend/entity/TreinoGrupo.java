package com.treino_abc_backend.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "treino_grupo")
public class TreinoGrupo {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    // Getters e Setters

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }
}
