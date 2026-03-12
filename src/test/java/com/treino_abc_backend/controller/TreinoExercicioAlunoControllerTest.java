package com.treino_abc_backend.controller;

import com.treino_abc_backend.entity.TreinoExercicioAluno;
import com.treino_abc_backend.enums.StatusExecucaoExercicio;
import com.treino_abc_backend.repository.TreinoExercicioAlunoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreinoExercicioAlunoControllerTest {


    @Mock
    private TreinoExercicioAlunoRepository repository;

    @InjectMocks
    private TreinoExercicioAlunoController controller;

    private UUID id;
    private TreinoExercicioAluno treinoExercicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();
        treinoExercicio = new TreinoExercicioAluno();
        treinoExercicio.setId(id);
        treinoExercicio.setStatus(StatusExecucaoExercicio.AGENDADO);
    }

    @Test
    void atualizarStatus_sucesso() {
        when(repository.findById(id)).thenReturn(Optional.of(treinoExercicio));
        Map<String, String> body = Map.of("status", "CONCLUIDO");
        ResponseEntity<?> response = controller.atualizarStatus(id, body);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(repository).findById(id);
        verify(repository).save(treinoExercicio);
        assertEquals(StatusExecucaoExercicio.CONCLUIDO, treinoExercicio.getStatus());
    }

    @Test
    void atualizarStatus_statusInvalido() {
        when(repository.findById(id)).thenReturn(Optional.of(treinoExercicio));
        Map<String, String> body = Map.of("status", "INVALIDO");
        // Não deixar o teste quebrar por IllegalArgumentException
        ResponseEntity<?> response = controller.atualizarStatus(id, body);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Status inválido"));
        verify(repository).findById(id);
        verify(repository, never()).save(any());
    }

    @Test
    void atualizarStatus_semStatus() {
        when(repository.findById(id)).thenReturn(Optional.of(treinoExercicio));
        Map<String, String> body = Map.of();
        ResponseEntity<?> response = controller.atualizarStatus(id, body);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Campo 'status' obrigatório", response.getBody());
        verify(repository).findById(id);
        verify(repository, never()).save(any());
    }

    @Test
    void atualizarStatus_erroInterno() {
        when(repository.findById(id)).thenThrow(new RuntimeException("Falha DB"));
        Map<String, String> body = Map.of("status", "CONCLUIDO");
        ResponseEntity<?> response = controller.atualizarStatus(id, body);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Erro interno"));
        verify(repository).findById(id);
        verify(repository, never()).save(any());
    }
}
