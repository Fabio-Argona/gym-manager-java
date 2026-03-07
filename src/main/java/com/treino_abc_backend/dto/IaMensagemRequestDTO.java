package com.treino_abc_backend.dto;

import java.util.UUID;

public class IaMensagemRequestDTO {

    private UUID alunoId;
    private String mensagem;

    public UUID getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(UUID alunoId) {
        this.alunoId = alunoId;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
