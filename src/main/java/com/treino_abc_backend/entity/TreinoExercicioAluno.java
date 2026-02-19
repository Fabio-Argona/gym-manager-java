package com.treino_abc_backend.entity;

import com.treino_abc_backend.enums.StatusExecucaoExercicio;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "treino_exercicio_aluno")
public class TreinoExercicioAluno {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "aluno_id", nullable = false)
    private UUID alunoId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    private TreinoGrupo grupo;

    // -----------------------------
    // CONTROLE DE EXECUÇÃO
    // -----------------------------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusExecucaoExercicio status = StatusExecucaoExercicio.AGENDADO;

    // -----------------------------
    // CONFIGURAÇÃO DO TREINO
    // -----------------------------

    @Column(nullable = false)
    private String diaSemana;

    @Column(nullable = false)
    private Integer ordem;

    @Column(length = 500)
    private String observacao;

    // Snapshot (caso o aluno altere carga/execução)
    @Column(name = "nome_exercicio")
    private String nomeExercicio;

    private Integer series;

    private Integer repeticoes;

    @Column(name = "peso_inicial")
    private Double pesoInicial;

    // -----------------------------
    // HISTÓRICO DE EXECUÇÃO
    // -----------------------------

    @ElementCollection
    @CollectionTable(
            name = "treino_datas",
            joinColumns = @JoinColumn(name = "treino_exercicio_aluno_id")
    )
    @Column(name = "data_treino")
    private List<LocalDate> datasTreinadas = new ArrayList<>();

    public TreinoExercicioAluno() {}

    // -----------------------------
    // GETTERS E SETTERS
    // -----------------------------

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAlunoId() { return alunoId; }
    public void setAlunoId(UUID alunoId) { this.alunoId = alunoId; }

    public Exercicio getExercicio() { return exercicio; }
    public void setExercicio(Exercicio exercicio) { this.exercicio = exercicio; }

    public TreinoGrupo getGrupo() { return grupo; }
    public void setGrupo(TreinoGrupo grupo) { this.grupo = grupo; }

    public StatusExecucaoExercicio getStatus() { return status; }
    public void setStatus(StatusExecucaoExercicio status) { this.status = status; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public String getNomeExercicio() { return nomeExercicio; }
    public void setNomeExercicio(String nomeExercicio) { this.nomeExercicio = nomeExercicio; }

    public Integer getSeries() { return series; }
    public void setSeries(Integer series) { this.series = series; }

    public Integer getRepeticoes() { return repeticoes; }
    public void setRepeticoes(Integer repeticoes) { this.repeticoes = repeticoes; }

    public Double getPesoInicial() { return pesoInicial; }
    public void setPesoInicial(Double pesoInicial) { this.pesoInicial = pesoInicial; }

    public List<LocalDate> getDatasTreinadas() { return datasTreinadas; }
    public void setDatasTreinadas(List<LocalDate> datasTreinadas) { this.datasTreinadas = datasTreinadas; }
}
