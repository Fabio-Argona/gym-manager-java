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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    private TreinoGrupo grupo;

    @Column(name = "aluno_id", nullable = false)
    private UUID alunoId;

    @Column(nullable = false)
    private LocalDate data;

    // Getters e setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public TreinoGrupo getGrupo() { return grupo; }
    public void setGrupo(TreinoGrupo grupo) { this.grupo = grupo; }

    public UUID getAlunoId() { return alunoId; }
    public void setAlunoId(UUID alunoId) { this.alunoId = alunoId; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
}
