package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class AlunoRegisterDTO {

    private String nome;

    private String cpf;

    private String email;

    private String telefone;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @JsonProperty("data_nascimento")
    private LocalDate dataNascimento;

    private String login;

    private String password;

    // Construtor padrão
    public AlunoRegisterDTO() {}

    // Construtor completo
    public AlunoRegisterDTO(String nome, String cpf, String email, String telefone,
                            LocalDate dataNascimento, String login, String password) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.login = login;
        this.password = password;
    }

    // Getters e Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
