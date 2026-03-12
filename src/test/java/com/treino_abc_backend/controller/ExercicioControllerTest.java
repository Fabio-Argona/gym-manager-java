package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.ExercicioDTO;
import com.treino_abc_backend.dto.ExercicioEdicaoDTO;
import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.service.ExercicioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExercicioControllerTest {

    @Mock
    private ExercicioService service;

    @InjectMocks
    private ExercicioController controller;

    private UUID alunoId;
    private UUID exercicioId;
    private ExercicioDTO exercicioDTO;
    private Exercicio exercicio;
    private ExercicioEdicaoDTO exercicioEdicaoDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        alunoId = UUID.randomUUID();
        exercicioId = UUID.randomUUID();
        exercicioDTO = new ExercicioDTO();
        exercicioDTO.setAlunoId(alunoId);
        exercicio = new Exercicio();
        exercicioEdicaoDTO = new ExercicioEdicaoDTO();
    }

    @Test
    void listar_deveRetornarListaDeExercicios() {
        List<ExercicioDTO> lista = Arrays.asList(exercicioDTO);
        when(service.getPorAluno(alunoId)).thenReturn(lista);
        ResponseEntity<List<ExercicioDTO>> response = controller.listar(alunoId.toString());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(lista, response.getBody());
        verify(service).getPorAluno(alunoId);
    }

    @Test
    void criar_deveRetornarExercicioCriado() {
        when(service.criarExercicio(any(ExercicioDTO.class))).thenReturn(exercicio);
        when(service.toDTO(exercicio)).thenReturn(exercicioDTO);
        ResponseEntity<ExercicioDTO> response = controller.criar(alunoId.toString(), exercicioDTO);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(exercicioDTO, response.getBody());
        ArgumentCaptor<ExercicioDTO> captor = ArgumentCaptor.forClass(ExercicioDTO.class);
        verify(service).criarExercicio(captor.capture());
        assertEquals(alunoId, captor.getValue().getAlunoId());
        verify(service).toDTO(exercicio);
    }

    @Test
    void atualizar_deveRetornarExercicioAtualizado() {
        when(service.atualizar(exercicioId, exercicioEdicaoDTO, alunoId)).thenReturn(exercicio);
        when(service.toDTO(exercicio)).thenReturn(exercicioDTO);
        ResponseEntity<ExercicioDTO> response = controller.atualizar(exercicioId, exercicioEdicaoDTO, alunoId.toString());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(exercicioDTO, response.getBody());
        verify(service).atualizar(exercicioId, exercicioEdicaoDTO, alunoId);
        verify(service).toDTO(exercicio);
    }

    @Test
    void deletar_deveRetornarNoContent() {
        doNothing().when(service).deletar(exercicioId, alunoId);
        ResponseEntity<Void> response = controller.deletar(alunoId.toString(), exercicioId);
        assertEquals(204, response.getStatusCodeValue());
        verify(service).deletar(exercicioId, alunoId);
    }

    @Test
    void listar_deveLancarExcecaoParaAlunoIdInvalido() {
        String invalidId = "invalid-uuid";
        assertThrows(IllegalArgumentException.class, () -> controller.listar(invalidId));
    }

    @Test
    void criar_deveLancarExcecaoParaAlunoIdInvalido() {
        String invalidId = "invalid-uuid";
        assertThrows(IllegalArgumentException.class, () -> controller.criar(invalidId, exercicioDTO));
    }

    @Test
    void atualizar_deveLancarExcecaoParaAlunoIdInvalido() {
        String invalidId = "invalid-uuid";
        assertThrows(IllegalArgumentException.class, () -> controller.atualizar(exercicioId, exercicioEdicaoDTO, invalidId));
    }

    @Test
    void deletar_deveLancarExcecaoParaAlunoIdInvalido() {
        String invalidId = "invalid-uuid";
        assertThrows(IllegalArgumentException.class, () -> controller.deletar(invalidId, exercicioId));
    }
}
