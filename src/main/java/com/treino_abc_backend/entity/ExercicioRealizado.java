package com.treino_abc_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "exercicio_realizado")
public class ExercicioRealizado {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "treino_realizado_id", nullable = false)
    private TreinoRealizado treinoRealizado;

    @ManyToOne
    @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio;

    @Column(nullable = false)
    private Integer seriesRealizadas;

    @Column(nullable = false)
    private Integer repeticoesRealizadas;

    @Column(nullable = false)
    private Double pesoUtilizado;

    private String observacoes;

    @Column(nullable = false)
    private LocalDate dataSessao;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime criadoEm;

    public ExercicioRealizado() {}

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public TreinoRealizado getTreinoRealizado() { return treinoRealizado; }
    public void setTreinoRealizado(TreinoRealizado treinoRealizado) { this.treinoRealizado = treinoRealizado; }

    public Exercicio getExercicio() { return exercicio; }
    public void setExercicio(Exercicio exercicio) { this.exercicio = exercicio; }

    public Integer getSeriesRealizadas() { return seriesRealizadas; }
    public void setSeriesRealizadas(Integer seriesRealizadas) { this.seriesRealizadas = seriesRealizadas; }

    public Integer getRepeticoesRealizadas() { return repeticoesRealizadas; }
    public void setRepeticoesRealizadas(Integer repeticoesRealizadas) { this.repeticoesRealizadas = repeticoesRealizadas; }

    public Double getPesoUtilizado() { return pesoUtilizado; }
    public void setPesoUtilizado(Double pesoUtilizado) { this.pesoUtilizado = pesoUtilizado; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDate getDataSessao() { return dataSessao; }
    public void setDataSessao(LocalDate dataSessao) { this.dataSessao = dataSessao; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
