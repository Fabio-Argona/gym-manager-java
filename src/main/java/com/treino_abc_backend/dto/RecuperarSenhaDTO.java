package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecuperarSenhaDTO {

    @JsonProperty("email")
    private String email;

    // Construtor padrão
    public RecuperarSenhaDTO() {}

    // Construtor completo
    public RecuperarSenhaDTO(String email) {
        this.email = email;
    }

    // Getters e Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
