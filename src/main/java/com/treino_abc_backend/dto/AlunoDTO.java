package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class AlunoDTO {

    private UUID id;

    private String nome;

    private String cpf;

    private String email;

    private String telefone;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("data_nascimento")
    private LocalDate dataNascimento;

    private String login;

    // ======= CAMPOS FÍSICOS =======

    private String sexo;

    private Double pesoAtual;
    private Double altura;
    private Double percentualGordura;
    private Double percentualMusculo;
    private Double imc;

    // ======= MEDIDAS CORPORAIS =======

    private Double cintura;
    private Double abdomen;
    private Double quadril;
    private Double peito;
    private Double ombro;
    private Double bracoDireito;
    private Double bracoEsquerdo;
    private Double coxaDireita;
    private Double coxaEsquerda;
    private Double panturrilhaDireita;
    private Double panturrilhaEsquerda;

    // ======= OBJETIVO E NÍVEL =======

    private String objetivo;           // Ex.: "Hipertrofia", "Emagrecimento", "Resistência"
    private String nivelTreinamento;   // Ex.: "Iniciante", "Intermediário", "Avançado"

    // ======= HISTÓRICO DE EVOLUÇÃO =======

    private List<EvolucaoMedidasDTO> historicoEvolucao;

    // ======= CONSTRUTOR PADRÃO =======
    public AlunoDTO() {}

    // ======= CONSTRUTOR BASE =======
    public AlunoDTO(UUID id, String nome, String cpf, String email, String telefone,
                    LocalDate dataNascimento, String login) {

        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.login = login;
    }

    // ======= GETTERS E SETTERS =======

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }

    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }

    public void setTelefone(String telefone) { this.telefone = telefone; }

    public LocalDate getDataNascimento() { return dataNascimento; }

    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getLogin() { return login; }

    public void setLogin(String login) { this.login = login; }

    // Físico
    public String getSexo() { return sexo; }

    public void setSexo(String sexo) { this.sexo = sexo; }

    public Double getPesoAtual() { return pesoAtual; }

    public void setPesoAtual(Double pesoAtual) { this.pesoAtual = pesoAtual; }

    public Double getAltura() { return altura; }

    public void setAltura(Double altura) { this.altura = altura; }

    public Double getPercentualGordura() { return percentualGordura; }

    public void setPercentualGordura(Double percentualGordura) { this.percentualGordura = percentualGordura; }

    public Double getPercentualMusculo() { return percentualMusculo; }

    public void setPercentualMusculo(Double percentualMusculo) { this.percentualMusculo = percentualMusculo; }

    public Double getImc() { return imc; }

    public void setImc(Double imc) { this.imc = imc; }

    // Medidas
    public Double getCintura() { return cintura; }

    public void setCintura(Double cintura) { this.cintura = cintura; }

    public Double getAbdomen() { return abdomen; }

    public void setAbdomen(Double abdomen) { this.abdomen = abdomen; }

    public Double getQuadril() { return quadril; }

    public void setQuadril(Double quadril) { this.quadril = quadril; }

    public Double getPeito() { return peito; }

    public void setPeito(Double peito) { this.peito = peito; }

    public Double getOmbro() { return ombro; }

    public void setOmbro(Double ombro) { this.ombro = ombro; }

    public Double getBracoDireito() { return bracoDireito; }

    public void setBracoDireito(Double bracoDireito) { this.bracoDireito = bracoDireito; }

    public Double getBracoEsquerdo() { return bracoEsquerdo; }

    public void setBracoEsquerdo(Double bracoEsquerdo) { this.bracoEsquerdo = bracoEsquerdo; }

    public Double getCoxaDireita() { return coxaDireita; }

    public void setCoxaDireita(Double coxaDireita) { this.coxaDireita = coxaDireita; }

    public Double getCoxaEsquerda() { return coxaEsquerda; }

    public void setCoxaEsquerda(Double coxaEsquerda) { this.coxaEsquerda = coxaEsquerda; }

    public Double getPanturrilhaDireita() { return panturrilhaDireita; }

    public void setPanturrilhaDireita(Double panturrilhaDireita) { this.panturrilhaDireita = panturrilhaDireita; }

    public Double getPanturrilhaEsquerda() { return panturrilhaEsquerda; }

    public void setPanturrilhaEsquerda(Double panturrilhaEsquerda) { this.panturrilhaEsquerda = panturrilhaEsquerda; }

    // Objetivo e nível
    public String getObjetivo() { return objetivo; }

    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public String getNivelTreinamento() { return nivelTreinamento; }

    public void setNivelTreinamento(String nivelTreinamento) { this.nivelTreinamento = nivelTreinamento; }

    // Histórico
    public List<EvolucaoMedidasDTO> getHistoricoEvolucao() { return historicoEvolucao; }

    public void setHistoricoEvolucao(List<EvolucaoMedidasDTO> historicoEvolucao) { this.historicoEvolucao = historicoEvolucao; }
}
