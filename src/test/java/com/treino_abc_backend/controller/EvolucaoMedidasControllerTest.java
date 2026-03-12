package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.EvolucaoMedidasDTO;
import com.treino_abc_backend.entity.EvolucaoMedidas;
import com.treino_abc_backend.service.EvolucaoMedidasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EvolucaoMedidasControllerTest {
    @Mock
    private EvolucaoMedidasService evolucaoService;
    @InjectMocks
    private EvolucaoMedidasController controller;

    private UUID id;
    private UUID alunoId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();
        alunoId = UUID.randomUUID();
    }

    @Test
    void salvar_deveRetornarEvolucaoMedidas() {
        EvolucaoMedidasDTO dto = new EvolucaoMedidasDTO();
        EvolucaoMedidas salvo = new EvolucaoMedidas();
        when(evolucaoService.salvar(dto)).thenReturn(salvo);
        ResponseEntity<?> response = controller.salvar(dto);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(salvo, response.getBody());
    }

    @Test
    void salvar_deveRetornarBadRequest() {
        EvolucaoMedidasDTO dto = new EvolucaoMedidasDTO();
        when(evolucaoService.salvar(dto)).thenThrow(new RuntimeException("Erro salvar"));
        ResponseEntity<?> response = controller.salvar(dto);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Erro salvar"));
    }

    @Test
    void atualizar_deveRetornarEvolucaoMedidas() {
        EvolucaoMedidasDTO dto = new EvolucaoMedidasDTO();
        EvolucaoMedidas atualizado = new EvolucaoMedidas();
        when(evolucaoService.atualizar(id, dto)).thenReturn(atualizado);
        ResponseEntity<?> response = controller.atualizar(id, dto);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(atualizado, response.getBody());
    }

    @Test
    void atualizar_deveRetornarBadRequest() {
        EvolucaoMedidasDTO dto = new EvolucaoMedidasDTO();
        when(evolucaoService.atualizar(id, dto)).thenThrow(new RuntimeException("Erro atualizar"));
        ResponseEntity<?> response = controller.atualizar(id, dto);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Erro atualizar"));
    }

    @Test
    void listarPorAluno_deveRetornarLista() {
        List<EvolucaoMedidas> lista = List.of(new EvolucaoMedidas());
        when(evolucaoService.listarPorAluno(alunoId)).thenReturn(lista);
        ResponseEntity<?> response = controller.listarPorAluno(alunoId);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(lista, response.getBody());
    }

    @Test
    void listarPorAluno_deveRetornarErro404() {
        when(evolucaoService.listarPorAluno(alunoId)).thenThrow(new RuntimeException("Aluno não encontrado"));
        ResponseEntity<?> response = controller.listarPorAluno(alunoId);
        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Aluno não encontrado"));
    }
}
