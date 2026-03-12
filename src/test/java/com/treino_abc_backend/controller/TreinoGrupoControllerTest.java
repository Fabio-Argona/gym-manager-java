package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.TreinoGrupoDTO;
import com.treino_abc_backend.service.TreinoGrupoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreinoGrupoControllerTest {

    @Mock
    private TreinoGrupoService grupoService;

    @InjectMocks
    private TreinoGrupoController controller;

    private UUID grupoId;
    private UUID alunoId;
    private TreinoGrupoDTO grupoDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        grupoId = UUID.randomUUID();
        alunoId = UUID.randomUUID();
        grupoDTO = new TreinoGrupoDTO(grupoId, alunoId, "Grupo Teste");
    }

    @Test
    void criar_sucesso() {
        when(grupoService.criar(any())).thenReturn(grupoDTO);
        ResponseEntity<TreinoGrupoDTO> response = controller.criar(grupoDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(grupoDTO, response.getBody());
        verify(grupoService).criar(grupoDTO);
    }

    @Test
    void listarPorAluno_sucesso() {
        List<TreinoGrupoDTO> grupos = Arrays.asList(grupoDTO);
        when(grupoService.listarPorAluno(alunoId)).thenReturn(grupos);
        ResponseEntity<List<TreinoGrupoDTO>> response = controller.listarPorAluno(alunoId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(grupos, response.getBody());
        verify(grupoService).listarPorAluno(alunoId);
    }

    @Test
    void remover_sucesso() {
        doNothing().when(grupoService).remover(grupoId);
        ResponseEntity<Void> response = controller.remover(grupoId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(grupoService).remover(grupoId);
    }

    @Test
    void editarGrupo_sucesso() {
        when(grupoService.editar(eq(grupoId), any())).thenReturn(grupoDTO);
        ResponseEntity<TreinoGrupoDTO> response = controller.editarGrupo(grupoId, grupoDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(grupoDTO, response.getBody());
        verify(grupoService).editar(grupoId, grupoDTO);
    }

    @Test
    void editarGrupo_notFound() {
        when(grupoService.editar(eq(grupoId), any())).thenThrow(new IllegalArgumentException("Grupo não encontrado"));
        ResponseEntity<TreinoGrupoDTO> response = controller.editarGrupo(grupoId, grupoDTO);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(grupoService).editar(grupoId, grupoDTO);
    }

    @Test
    void editarGrupo_erroInterno() {
        when(grupoService.editar(eq(grupoId), any())).thenThrow(new RuntimeException("Erro interno"));
        ResponseEntity<TreinoGrupoDTO> response = controller.editarGrupo(grupoId, grupoDTO);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(grupoService).editar(grupoId, grupoDTO);
    }

    @Test
    void excluirGrupoComExercicios_sucesso() {
        doNothing().when(grupoService).excluirGrupoComExercicios(grupoId);
        ResponseEntity<?> response = controller.excluirGrupoComExercicios(grupoId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(grupoService).excluirGrupoComExercicios(grupoId);
    }

    @Test
    void excluirGrupoComExercicios_erroInterno() {
        doThrow(new RuntimeException("Falha ao excluir")).when(grupoService).excluirGrupoComExercicios(grupoId);
        ResponseEntity<?> response = controller.excluirGrupoComExercicios(grupoId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Erro ao excluir grupo e exercícios"));
        verify(grupoService).excluirGrupoComExercicios(grupoId);
    }
}
