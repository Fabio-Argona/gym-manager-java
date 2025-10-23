package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.UUID;

public class AlunoDTO {

    private UUID id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    private String login;

    // Construtor padrão
    public AlunoDTO() {}

    // Construtor completo
    public AlunoDTO(UUID id, String nome, String cpf, String email, String telefone, LocalDate dataNascimento, String login) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.login = login;
    }

    // Getters e Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
