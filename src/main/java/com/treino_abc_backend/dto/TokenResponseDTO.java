package com.treino_abc_backend.dto;

public class TokenResponseDTO {

    private String token;
    private AlunoDTO aluno;

    // Construtor padrão
    public TokenResponseDTO() {}

    // Construtor completo
    public TokenResponseDTO(String token, AlunoDTO aluno) {
        this.token = token;
        this.aluno = aluno;
    }

    // Construtor com apenas o token
    public TokenResponseDTO(String token) {
        this.token = token;
    }

    // Getters e Setters

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
