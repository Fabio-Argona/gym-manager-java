package com.treino_abc_backend.controller;

import com.treino_abc_backend.entity.TreinoRealizado;
import com.treino_abc_backend.service.TreinoRealizadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreinoControllerTest {

    @Mock
    private TreinoRealizadoService realizadoService;
    @InjectMocks
    private TreinoController controller;

    private UUID grupoId;
    private UUID alunoId;
    private TreinoRealizado realizado;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        grupoId = UUID.randomUUID();
        alunoId = UUID.randomUUID();
        realizado = new TreinoRealizado();
        realizado.setId(UUID.randomUUID());
        realizado.setGrupo(mockGrupo(grupoId));
        realizado.setAlunoId(alunoId);
        realizado.setData(LocalDate.now());
    }

    private com.treino_abc_backend.entity.TreinoGrupo mockGrupo(UUID grupoId) {
        com.treino_abc_backend.entity.TreinoGrupo grupo = new com.treino_abc_backend.entity.TreinoGrupo();
        grupo.setId(grupoId);
        grupo.setNome("Grupo Teste");
        return grupo;
    }

    @Test
    void registrarTreino_sucesso() {
        when(realizadoService.registrarPorGrupo(grupoId, LocalDate.now())).thenReturn(realizado);
        ResponseEntity<?> response = controller.registrarTreino(grupoId, null);
        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals(grupoId.toString(), body.get("grupoId"));
        verify(realizadoService).registrarPorGrupo(grupoId, LocalDate.now());
    }

    @Test
    void registrarTreino_comData() {
        LocalDate data = LocalDate.of(2024, 1, 1);
        when(realizadoService.registrarPorGrupo(grupoId, data)).thenReturn(realizado);
        ResponseEntity<?> response = controller.registrarTreino(grupoId, "2024-01-01");
        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals(grupoId.toString(), body.get("grupoId"));
        verify(realizadoService).registrarPorGrupo(grupoId, data);
    }

    @Test
    void registrarTreino_badRequest() {
        when(realizadoService.registrarPorGrupo(grupoId, LocalDate.now())).thenThrow(new IllegalArgumentException("Erro"));
        ResponseEntity<?> response = controller.registrarTreino(grupoId, null);
        assertEquals(400, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Bad Request", body.get("error"));
        assertEquals(400, body.get("status"));
        verify(realizadoService).registrarPorGrupo(grupoId, LocalDate.now());
    }

    @Test
    void listarSessoesPorAluno_sucesso() {
        TreinoRealizado tr = new TreinoRealizado();
        tr.setId(UUID.randomUUID());
        tr.setData(LocalDate.now());
        tr.setGrupo(mockGrupo(grupoId));
        tr.setAlunoId(alunoId);
        List<TreinoRealizado> sessoes = Arrays.asList(tr);
        when(realizadoService.buscarSessoesPorAluno(alunoId)).thenReturn(sessoes);
        ResponseEntity<List<Map<String, String>>> response = controller.listarSessoesPorAluno(alunoId);
        assertEquals(200, response.getStatusCodeValue());
        List<Map<String, String>> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        assertEquals("Grupo Teste", body.get(0).get("grupoNome"));
        verify(realizadoService).buscarSessoesPorAluno(alunoId);
    }

    @Test
    void buscarUltimaSessao_sucesso() {
        when(realizadoService.buscarUltimaPorGrupo(grupoId)).thenReturn(Optional.of(realizado));
        ResponseEntity<?> response = controller.buscarUltimaSessao(grupoId);
        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals(grupoId.toString(), body.get("grupoId"));
        verify(realizadoService).buscarUltimaPorGrupo(grupoId);
    }

    @Test
    void buscarUltimaSessao_notFound() {
        when(realizadoService.buscarUltimaPorGrupo(grupoId)).thenReturn(Optional.empty());
        ResponseEntity<?> response = controller.buscarUltimaSessao(grupoId);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(realizadoService).buscarUltimaPorGrupo(grupoId);
    }

    // ...existing code...
}
