package com.treino_abc_backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ExercicioRealizadoDTO {

    private UUID id;
    private UUID exercicioId;
    private String nomeExercicio;
    private Integer seriesRealizadas;
    private Integer repeticoesRealizadas;
    private Double pesoUtilizado;
    private String observacoes;
    private LocalDate dataSessao;
    private LocalDateTime criadoEm;

    public ExercicioRealizadoDTO() {}

    public ExercicioRealizadoDTO(UUID id, UUID exercicioId, String nomeExercicio,
                                Integer seriesRealizadas, Integer repeticoesRealizadas,
                                Double pesoUtilizado, String observacoes, LocalDate dataSessao,
                                LocalDateTime criadoEm) {
        this.id = id;
        this.exercicioId = exercicioId;
        this.nomeExercicio = nomeExercicio;
        this.seriesRealizadas = seriesRealizadas;
        this.repeticoesRealizadas = repeticoesRealizadas;
        this.pesoUtilizado = pesoUtilizado;
        this.observacoes = observacoes;
        this.dataSessao = dataSessao;
        this.criadoEm = criadoEm;
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getExercicioId() { return exercicioId; }
    public void setExercicioId(UUID exercicioId) { this.exercicioId = exercicioId; }

    public String getNomeExercicio() { return nomeExercicio; }
    public void setNomeExercicio(String nomeExercicio) { this.nomeExercicio = nomeExercicio; }

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
