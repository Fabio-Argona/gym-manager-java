package com.treino_abc_backend.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO para registrar um exercício realizado pelo aluno
 * Recebe os dados do frontend quando o aluno completa um exercício
 */
public class RegistrarExercicioDTO {

    private UUID treino_realizado_id;  // ID do TreinoRealizado (sessão de treino)
    private UUID exercicio_id;          // ID do exercício
    private Integer series_realizadas;  // Séries que o aluno fez
    private Integer repeticoes_realizadas; // Repetições que o aluno fez
    private Double peso_utilizado;      // Peso usado
    private String observacoes;         // Observações (opcional)
    private LocalDate data_sessao;      // Data da sessão

    public RegistrarExercicioDTO() {}

    public RegistrarExercicioDTO(UUID treino_realizado_id, UUID exercicio_id,
                               Integer series_realizadas, Integer repeticoes_realizadas,
                               Double peso_utilizado, String observacoes, LocalDate data_sessao) {
        this.treino_realizado_id = treino_realizado_id;
        this.exercicio_id = exercicio_id;
        this.series_realizadas = series_realizadas;
        this.repeticoes_realizadas = repeticoes_realizadas;
        this.peso_utilizado = peso_utilizado;
        this.observacoes = observacoes;
        this.data_sessao = data_sessao;
    }

    // Getters e Setters
    public UUID getTreino_realizado_id() { return treino_realizado_id; }
    public void setTreino_realizado_id(UUID treino_realizado_id) { this.treino_realizado_id = treino_realizado_id; }

    public UUID getExercicio_id() { return exercicio_id; }
    public void setExercicio_id(UUID exercicio_id) { this.exercicio_id = exercicio_id; }

    public Integer getSeries_realizadas() { return series_realizadas; }
    public void setSeries_realizadas(Integer series_realizadas) { this.series_realizadas = series_realizadas; }

    public Integer getRepeticoes_realizadas() { return repeticoes_realizadas; }
    public void setRepeticoes_realizadas(Integer repeticoes_realizadas) { this.repeticoes_realizadas = repeticoes_realizadas; }

    public Double getPeso_utilizado() { return peso_utilizado; }
    public void setPeso_utilizado(Double peso_utilizado) { this.peso_utilizado = peso_utilizado; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDate getData_sessao() { return data_sessao; }
    public void setData_sessao(LocalDate data_sessao) { this.data_sessao = data_sessao; }
}
