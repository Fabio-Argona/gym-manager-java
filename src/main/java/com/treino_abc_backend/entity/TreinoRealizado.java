package com.treino_abc_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "treino_realizado")
public class TreinoRealizado {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "treino_aluno_id", nullable = false)
    private TreinoExercicioAluno treino;

    @Column(nullable = false)
    private LocalDate data;

    // Getters e setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public TreinoExercicioAluno getTreino() { return treino; }
    public void setTreino(TreinoExercicioAluno treino) { this.treino = treino; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
}
