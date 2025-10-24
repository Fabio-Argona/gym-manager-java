package com.treino_abc_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponseDTO {

    @JsonProperty("token")
    private String token;

    @JsonProperty("aluno")
    private AlunoDTO aluno;

    public TokenResponseDTO() {}

    public TokenResponseDTO(String token, AlunoDTO aluno) {
        this.token = token;
        this.aluno = aluno;
    }

    public TokenResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AlunoDTO getAluno() {
        return aluno;
    }

    public void setAluno(AlunoDTO aluno) {
        this.aluno = aluno;
    }
}
