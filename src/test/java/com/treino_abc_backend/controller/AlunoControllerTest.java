package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.AlunoDTO;
import com.treino_abc_backend.dto.AlunoUpdateDTO;
import com.treino_abc_backend.service.AlunoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlunoControllerTest {
    @Mock
    private AlunoService alunoService;

    @InjectMocks
    private AlunoController alunoController;

    private UUID alunoId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        alunoId = UUID.randomUUID();
    }

    @Test
    void buscarPorId_deveRetornarAlunoDTO() {
        AlunoDTO dto = new AlunoDTO();
        when(alunoService.buscarPorId(alunoId)).thenReturn(dto);
        ResponseEntity<?> response = alunoController.buscarPorId(alunoId);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void buscarPorId_deveRetornarErro404() {
        when(alunoService.buscarPorId(alunoId)).thenThrow(new RuntimeException("Aluno não encontrado"));
        ResponseEntity<?> response = alunoController.buscarPorId(alunoId);
        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Aluno não encontrado"));
    }

    @Test
    void atualizarFisico_deveRetornarErro500() {
        Map<String, Object> body = Map.of("peso", 70);
        when(alunoService.atualizarFisico(alunoId, body)).thenThrow(new RuntimeException("Erro fisico"));
        ResponseEntity<?> response = alunoController.atualizarFisico(alunoId, body);
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Erro fisico"));
    }

    @Test
    void atualizarObjetivo_deveRetornarErro500() {
        Map<String, String> body = Map.of("objetivo", "Força", "nivelTreinamento", "Avançado");
        when(alunoService.atualizarObjetivo(alunoId, "Força", "Avançado")).thenThrow(new RuntimeException("Erro objetivo"));
        ResponseEntity<?> response = alunoController.atualizarObjetivo(alunoId, body);
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Erro objetivo"));
    }

    @Test
    void atualizarAluno_deveRetornarOk() {
        AlunoUpdateDTO updateDTO = new AlunoUpdateDTO();
        AlunoDTO dto = new AlunoDTO();
        when(alunoService.atualizarCompleto(alunoId, updateDTO)).thenReturn(dto);
        ResponseEntity<?> response = alunoController.atualizarAluno(alunoId, updateDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void atualizarAluno_deveRetornarErro404() {
        AlunoUpdateDTO updateDTO = new AlunoUpdateDTO();
        when(alunoService.atualizarCompleto(alunoId, updateDTO)).thenThrow(new RuntimeException("Erro update"));
        ResponseEntity<?> response = alunoController.atualizarAluno(alunoId, updateDTO);
        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Erro update"));
    }
}
