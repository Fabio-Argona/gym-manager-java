package com.treino_abc_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "evolucao_medidas")
public class EvolucaoMedidas {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    private LocalDate data;
    private Double peso;
    private Double percentualGordura;
    private Double percentualMusculo;
    private Double imc;
    private Double cintura;
    private Double abdomen;
    private Double quadril;
    private Double peito;
    private Double bracoDireito;
    private Double bracoEsquerdo;
    private Double coxaDireita;
    private Double coxaEsquerda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }

    public Double getPercentualGordura() { return percentualGordura; }
    public void setPercentualGordura(Double percentualGordura) { this.percentualGordura = percentualGordura; }

    public Double getPercentualMusculo() { return percentualMusculo; }
    public void setPercentualMusculo(Double percentualMusculo) { this.percentualMusculo = percentualMusculo; }

    public Double getImc() { return imc; }
    public void setImc(Double imc) { this.imc = imc; }

    public Double getCintura() { return cintura; }
    public void setCintura(Double cintura) { this.cintura = cintura; }

    public Double getAbdomen() { return abdomen; }
    public void setAbdomen(Double abdomen) { this.abdomen = abdomen; }

    public Double getQuadril() { return quadril; }
    public void setQuadril(Double quadril) { this.quadril = quadril; }

    public Double getPeito() { return peito; }
    public void setPeito(Double peito) { this.peito = peito; }

    public Double getBracoDireito() { return bracoDireito; }
    public void setBracoDireito(Double bracoDireito) { this.bracoDireito = bracoDireito; }

    public Double getBracoEsquerdo() { return bracoEsquerdo; }
    public void setBracoEsquerdo(Double bracoEsquerdo) { this.bracoEsquerdo = bracoEsquerdo; }

    public Double getCoxaDireita() { return coxaDireita; }
    public void setCoxaDireita(Double coxaDireita) { this.coxaDireita = coxaDireita; }

    public Double getCoxaEsquerda() { return coxaEsquerda; }
    public void setCoxaEsquerda(Double coxaEsquerda) { this.coxaEsquerda = coxaEsquerda; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }
}
