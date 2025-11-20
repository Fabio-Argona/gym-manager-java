package com.treino_abc_backend.dto;

import java.time.LocalDate;
import java.util.UUID;

public class EvolucaoMedidasDTO {

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
    private UUID alunoId;

    // Getters e Setters
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

    public UUID getAlunoId() { return alunoId; }
    public void setAlunoId(UUID alunoId) { this.alunoId = alunoId; }
}
