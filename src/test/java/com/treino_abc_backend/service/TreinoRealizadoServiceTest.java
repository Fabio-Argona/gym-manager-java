package com.treino_abc_backend.service;

import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.entity.TreinoGrupo;
import com.treino_abc_backend.entity.TreinoRealizado;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import com.treino_abc_backend.repository.TreinoRealizadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreinoRealizadoServiceTest {
    @Mock
    private TreinoRealizadoRepository realizadoRepo;
    @Mock
    private TreinoGrupoRepository grupoRepo;
    @InjectMocks
    private TreinoRealizadoService service;

    private UUID grupoId;
    private UUID alunoId;
    private TreinoGrupo grupo;
    private LocalDate data;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        grupoId = UUID.randomUUID();
        alunoId = UUID.randomUUID();
        grupo = new TreinoGrupo();
        grupo.setId(grupoId);
        Aluno aluno = new Aluno();
        aluno.setId(alunoId);
        grupo.setAluno(aluno);
        data = LocalDate.now();
    }

    @Test
    void registrarPorGrupo_sucesso() {
        when(grupoRepo.findById(grupoId)).thenReturn(Optional.of(grupo));
        when(realizadoRepo.findByTreinoAlunoId(alunoId)).thenReturn(new ArrayList<>());
        when(realizadoRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        TreinoRealizado realizado = service.registrarPorGrupo(grupoId, data);
        assertEquals(grupoId, realizado.getGrupo().getId());
        assertEquals(alunoId, realizado.getAlunoId());
        assertEquals(data, realizado.getData());
        verify(grupoRepo).findById(grupoId);
        verify(realizadoRepo).save(any());
    }

    @Test
    void registrarPorGrupo_jaExisteSessao() {
        when(grupoRepo.findById(grupoId)).thenReturn(Optional.of(grupo));
        TreinoRealizado existente = new TreinoRealizado();
        existente.setGrupo(grupo);
        existente.setAlunoId(alunoId);
        existente.setData(data);
        when(realizadoRepo.findByTreinoAlunoId(alunoId)).thenReturn(List.of(existente));
        TreinoRealizado result = service.registrarPorGrupo(grupoId, data);
        assertEquals(existente, result);
        verify(grupoRepo).findById(grupoId);
    }

    @Test
    void registrarPorGrupo_erroGrupoNaoEncontrado() {
        when(grupoRepo.findById(grupoId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.registrarPorGrupo(grupoId, data));
        verify(grupoRepo).findById(grupoId);
    }

    @Test
    void getDatasTreinadas_sucesso() {
        TreinoRealizado tr = new TreinoRealizado();
        tr.setData(data);
        when(realizadoRepo.findByTreinoAlunoId(alunoId)).thenReturn(List.of(tr));
        List<LocalDate> datas = service.getDatasTreinadas(alunoId);
        assertEquals(List.of(data), datas);
        verify(realizadoRepo).findByTreinoAlunoId(alunoId);
    }

    @Test
    void obterSessao_sucesso() {
        UUID treinoRealizadoId = UUID.randomUUID();
        TreinoRealizado tr = new TreinoRealizado();
        when(realizadoRepo.findById(treinoRealizadoId)).thenReturn(Optional.of(tr));
        Optional<TreinoRealizado> result = service.obterSessao(treinoRealizadoId);
        assertTrue(result.isPresent());
        assertEquals(tr, result.get());
        verify(realizadoRepo).findById(treinoRealizadoId);
    }

    @Test
    void obterSessao_naoEncontrado() {
        UUID treinoRealizadoId = UUID.randomUUID();
        when(realizadoRepo.findById(treinoRealizadoId)).thenReturn(Optional.empty());
        Optional<TreinoRealizado> result = service.obterSessao(treinoRealizadoId);
        assertFalse(result.isPresent());
        verify(realizadoRepo).findById(treinoRealizadoId);
    }

    @Test
    void buscarSessoesPorAluno_sucesso() {
        TreinoRealizado tr = new TreinoRealizado();
        when(realizadoRepo.findByTreinoAlunoId(alunoId)).thenReturn(List.of(tr));
        List<TreinoRealizado> result = service.buscarSessoesPorAluno(alunoId);
        assertEquals(1, result.size());
        verify(realizadoRepo).findByTreinoAlunoId(alunoId);
    }

    @Test
    void buscarUltimaPorGrupo_sucesso() {
        TreinoRealizado tr = new TreinoRealizado();
        when(realizadoRepo.findTopByGrupo_IdOrderByDataDesc(grupoId)).thenReturn(Optional.of(tr));
        Optional<TreinoRealizado> result = service.buscarUltimaPorGrupo(grupoId);
        assertTrue(result.isPresent());
        assertEquals(tr, result.get());
        verify(realizadoRepo).findTopByGrupo_IdOrderByDataDesc(grupoId);
    }

    @Test
    void buscarUltimaPorGrupo_naoEncontrado() {
        when(realizadoRepo.findTopByGrupo_IdOrderByDataDesc(grupoId)).thenReturn(Optional.empty());
        Optional<TreinoRealizado> result = service.buscarUltimaPorGrupo(grupoId);
        assertFalse(result.isPresent());
        verify(realizadoRepo).findTopByGrupo_IdOrderByDataDesc(grupoId);
    }
}
