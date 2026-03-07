package com.treino_abc_backend.dto;

import java.time.LocalDateTime;

public class IaRespostaDTO {

    private String resposta;
    private LocalDateTime timestamp;

    public IaRespostaDTO() {
    }

    public IaRespostaDTO(String resposta) {
        this.resposta = resposta;
        this.timestamp = LocalDateTime.now();
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
}
