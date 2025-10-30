package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AlunoLoginDTO {

    @JsonProperty("email")
    private String email;

    private String password;

    public AlunoLoginDTO() {}

    public AlunoLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
