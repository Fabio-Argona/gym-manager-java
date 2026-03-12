package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.ExercicioRealizadoDTO;
import com.treino_abc_backend.dto.RegistrarExercicioDTO;
import com.treino_abc_backend.service.ExercicioRealizadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExercicioRealizadoControllerTest {

    @Mock
    private ExercicioRealizadoService service;

    @InjectMocks
    private ExercicioRealizadoController controller;

    private RegistrarExercicioDTO registrarDTO;
    private ExercicioRealizadoDTO realizadoDTO;
    private UUID alunoId;
    private UUID exercicioId;
    private List<ExercicioRealizadoDTO> listaDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registrarDTO = new RegistrarExercicioDTO();
        realizadoDTO = new ExercicioRealizadoDTO();
        alunoId = UUID.randomUUID();
        exercicioId = UUID.randomUUID();
        listaDTO = Arrays.asList(realizadoDTO);
    }

    @Test
    void registrarExercicio_deveRetornarCreated() {
        when(service.registrarExercicio(registrarDTO)).thenReturn(realizadoDTO);
        ResponseEntity<ExercicioRealizadoDTO> response = controller.registrarExercicio(registrarDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(realizadoDTO, response.getBody());
        verify(service).registrarExercicio(registrarDTO);
    }

    @Test
    void registrarExercicio_deveRetornarBadRequestQuandoIllegalArgument() {
        when(service.registrarExercicio(registrarDTO)).thenThrow(new IllegalArgumentException());
        ResponseEntity<ExercicioRealizadoDTO> response = controller.registrarExercicio(registrarDTO);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(service).registrarExercicio(registrarDTO);
    }

    @Test
    void buscarProgressao_deveRetornarTodaProgressao() {
        when(service.buscarTodaProgressao(alunoId)).thenReturn(listaDTO);
        ResponseEntity<List<ExercicioRealizadoDTO>> response = controller.buscarProgressao(alunoId, null, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listaDTO, response.getBody());
        verify(service).buscarTodaProgressao(alunoId);
    }

    @Test
    void buscarProgressao_deveRetornarHistoricoExercicio() {
        when(service.buscarHistoricoExercicio(alunoId, exercicioId)).thenReturn(listaDTO);
        ResponseEntity<List<ExercicioRealizadoDTO>> response = controller.buscarProgressao(alunoId, exercicioId, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listaDTO, response.getBody());
        verify(service).buscarHistoricoExercicio(alunoId, exercicioId);
    }

    @Test
    void buscarProgressao_deveRetornarProgressaoExercicioComDatas() {
        String dataInicio = "2024-01-01";
        String dataFim = "2024-01-31";
        LocalDate inicio = LocalDate.parse(dataInicio);
        LocalDate fim = LocalDate.parse(dataFim);
        when(service.buscarProgressaoExercicio(alunoId, exercicioId, inicio, fim)).thenReturn(listaDTO);
        ResponseEntity<List<ExercicioRealizadoDTO>> response = controller.buscarProgressao(alunoId, exercicioId, dataInicio, dataFim);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listaDTO, response.getBody());
        verify(service).buscarProgressaoExercicio(alunoId, exercicioId, inicio, fim);
    }

    @Test
    void buscarProgressao_deveRetornarBadRequestQuandoException() {
        when(service.buscarTodaProgressao(alunoId)).thenThrow(new RuntimeException());
        ResponseEntity<List<ExercicioRealizadoDTO>> response = controller.buscarProgressao(alunoId, null, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(service).buscarTodaProgressao(alunoId);
    }
}
