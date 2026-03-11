package com.treino_abc_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class IaRespostaDTO {

    private String resposta;
    private LocalDateTime timestamp;

    /**
     * Indica ao cliente que o aluno ainda não possui treinos/exercícios
     * cadastrados.
     * Quando true, o Flutter deve exibir os botões de opção (opcoes).
     */
    private boolean semTreinos = false;

    /**
     * Lista de ações sugeridas quando o aluno não tem treinos.
     * Ex: ["Criar treino e exercícios", "Perguntar algo sobre treino"]
     */
    private List<String> opcoes;

    public IaRespostaDTO() {
    }

    public IaRespostaDTO(String resposta) {
        this.resposta = resposta;
        this.timestamp = LocalDateTime.now();
    }

    public IaRespostaDTO(String resposta, boolean semTreinos, List<String> opcoes) {
        this.resposta = resposta;
        this.timestamp = LocalDateTime.now();
        this.semTreinos = semTreinos;
        this.opcoes = opcoes;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSemTreinos() {
        return semTreinos;
    }

    public void setSemTreinos(boolean semTreinos) {
        this.semTreinos = semTreinos;
    }

    public List<String> getOpcoes() {
        return opcoes;
    }

    public void setOpcoes(List<String> opcoes) {
        this.opcoes = opcoes;
    }
}
