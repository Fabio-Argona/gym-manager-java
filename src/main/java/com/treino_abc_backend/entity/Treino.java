package com.treino_abc_backend.entity;

import com.treino_abc_backend.enums.StatusTreino;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "treino")
public class Treino {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "aluno_id", nullable = false)
    private UUID alunoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTreino status = StatusTreino.AGENDADO;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    // getters e setters

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public UUID getAlunoId() { return alunoId; }
    public void setAlunoId(UUID alunoId) { this.alunoId = alunoId; }

    public StatusTreino getStatus() { return status; }
    public void setStatus(StatusTreino status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}

