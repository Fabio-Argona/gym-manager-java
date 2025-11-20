package com.treino_abc_backend.dto;

import com.treino_abc_backend.entity.EvolucaoMedidas;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class AlunoHistoricoDTO {

    private UUID id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private String login;

    private List<EvolucaoMedidas> evolucoes;

    // Getters e Setters
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

    public List<EvolucaoMedidas> getEvolucoes() { return evolucoes; }
    public void setEvolucoes(List<EvolucaoMedidas> evolucoes) { this.evolucoes = evolucoes; }
}
