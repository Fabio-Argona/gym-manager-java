package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RedefinirSenhaDTO {

    @JsonProperty("email")
    private String email;

    @JsonProperty("cpf6")
    private String cpf6;

    @JsonProperty("novaSenha")
    private String novaSenha;

    public RedefinirSenhaDTO() {}

    public RedefinirSenhaDTO(String email, String cpf6, String novaSenha) {
        this.email = email;
        this.cpf6 = cpf6;
        this.novaSenha = novaSenha;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCpf6() { return cpf6; }
    public void setCpf6(String cpf6) { this.cpf6 = cpf6; }

    public String getNovaSenha() { return novaSenha; }
    public void setNovaSenha(String novaSenha) { this.novaSenha = novaSenha; }
}
