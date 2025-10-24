package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RedefinirSenhaDTO {

    @JsonProperty("token")
    private String token;

    @JsonProperty("nova_senha")
    private String novaSenha;

    public RedefinirSenhaDTO() {}

    public RedefinirSenhaDTO(String token, String novaSenha) {
        this.token = token;
        this.novaSenha = novaSenha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }
}
