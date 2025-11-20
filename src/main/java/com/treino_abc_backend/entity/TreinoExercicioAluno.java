package com.treino_abc_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
public class TreinoExercicioAluno {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID alunoId;

    @ManyToOne
    @JoinColumn(name = "exercicio_id")
    private Exercicio exercicio;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private TreinoGrupo grupo;

    // --- CAMPOS QUE O SERVICE USA ---
    private String diaSemana;

    private Integer ordem;

    private String observacao;

    private String nomeExercicio;

    private Integer series;

    private Integer repeticoes;

    private Double pesoInicial;

    // Datas que já foram treinadas
    @ElementCollection
    @CollectionTable(
            name = "treino_datas",
            joinColumns = @JoinColumn(name = "treino_id")
    )
    @Column(name = "data_treino")
    private List<LocalDate> datasTreinadas = new ArrayList<>();

    public TreinoExercicioAluno() {}

    // ----------------------------------
    // GETTERS E SETTERS
    // ----------------------------------

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAlunoId() { return alunoId; }
    public void setAlunoId(UUID alunoId) { this.alunoId = alunoId; }

    public Exercicio getExercicio() { return exercicio; }
    public void setExercicio(Exercicio exercicio) { this.exercicio = exercicio; }

    public TreinoGrupo getGrupo() { return grupo; }
    public void setGrupo(TreinoGrupo grupo) { this.grupo = grupo; }

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
