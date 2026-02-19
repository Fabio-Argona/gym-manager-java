package com.treino_abc_backend.dto;

import com.treino_abc_backend.enums.StatusExecucaoExercicio;

public class StatusUpdateDTO {
    private StatusExecucaoExercicio status;

    public StatusExecucaoExercicio getStatus() {
        return status;
    }

    public void setStatus(StatusExecucaoExercicio status) {
        this.status = status;
    }
}

