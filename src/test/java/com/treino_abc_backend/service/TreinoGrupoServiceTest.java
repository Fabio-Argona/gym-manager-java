package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.TreinoGrupoDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.entity.TreinoGrupo;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.repository.ExercicioRepository;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreinoGrupoServiceTest {
    @Mock
    private TreinoGrupoRepository grupoRepo;
    @Mock
    private AlunoRepository alunoRepo;
    @Mock
    private ExercicioRepository exercicioRepo;
    @InjectMocks
    private TreinoGrupoService service;

    private UUID grupoId;
    private UUID alunoId;
    private Aluno aluno;
    private TreinoGrupo grupo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        grupoId = UUID.randomUUID();
        alunoId = UUID.randomUUID();
        aluno = new Aluno();
        aluno.setId(alunoId);
        grupo = new TreinoGrupo();
        grupo.setId(grupoId);
        grupo.setAluno(aluno);
        grupo.setNome("Grupo Teste");
        grupo.setAtivo(true);
    }

    @Test
    void criar_sucesso() {
        TreinoGrupoDTO dto = new TreinoGrupoDTO(null, alunoId, "Grupo Teste");
        when(alunoRepo.findById(alunoId)).thenReturn(Optional.of(aluno));
        when(grupoRepo.save(any())).thenReturn(grupo);
        TreinoGrupoDTO result = service.criar(dto);
        assertEquals(grupoId, result.getId());
        assertEquals(alunoId, result.getAlunoId());
        assertEquals("Grupo Teste", result.getNome());
        verify(alunoRepo).findById(alunoId);
        verify(grupoRepo).save(any());
    }

    @Test
    void criar_erroAlunoNaoEncontrado() {
        TreinoGrupoDTO dto = new TreinoGrupoDTO(null, alunoId, "Grupo Teste");
        when(alunoRepo.findById(alunoId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.criar(dto));
        verify(alunoRepo).findById(alunoId);
    }

    @Test
    void listarPorAluno_sucesso() {
        when(grupoRepo.findByAluno_IdAndAtivoTrue(alunoId)).thenReturn(List.of(grupo));
        List<TreinoGrupoDTO> result = service.listarPorAluno(alunoId);
        assertEquals(1, result.size());
        assertEquals(grupoId, result.get(0).getId());
        verify(grupoRepo).findByAluno_IdAndAtivoTrue(alunoId);
    }

    @Test
    void remover_sucesso() {
        when(grupoRepo.findById(grupoId)).thenReturn(Optional.of(grupo));
        when(grupoRepo.save(any())).thenReturn(grupo);
        service.remover(grupoId);
        assertFalse(grupo.isAtivo());
        verify(grupoRepo).findById(grupoId);
        verify(grupoRepo).save(grupo);
    }

    @Test
    void remover_erroGrupoNaoEncontrado() {
        when(grupoRepo.findById(grupoId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.remover(grupoId));
        verify(grupoRepo).findById(grupoId);
    }

    @Test
    void editar_sucesso() {
        TreinoGrupoDTO dto = new TreinoGrupoDTO(grupoId, alunoId, "Novo Nome");
        when(grupoRepo.findById(grupoId)).thenReturn(Optional.of(grupo));
        when(grupoRepo.save(any())).thenReturn(grupo);
        TreinoGrupoDTO result = service.editar(grupoId, dto);
        assertEquals("Novo Nome", result.getNome());
        verify(grupoRepo).findById(grupoId);
        verify(grupoRepo).save(grupo);
    }

    @Test
    void editar_erroGrupoNaoEncontrado() {
        TreinoGrupoDTO dto = new TreinoGrupoDTO(grupoId, alunoId, "Novo Nome");
        when(grupoRepo.findById(grupoId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.editar(grupoId, dto));
        verify(grupoRepo).findById(grupoId);
    }

    @Test
    void excluirGrupoComExercicios_sucesso() {
        Exercicio ex = new Exercicio();
        ex.setAtivo(true);
        ex.setGrupo(grupo);
        ex.setObservacao("Obs");
        List<Exercicio> exercicios = List.of(ex);
        when(exercicioRepo.findByGrupo_Id(grupoId)).thenReturn(exercicios);
        when(grupoRepo.findById(grupoId)).thenReturn(Optional.of(grupo));
        service.excluirGrupoComExercicios(grupoId);
        assertFalse(ex.isAtivo());
        assertNull(ex.getGrupo());
        assertTrue(ex.getObservacao().contains("Exercício desativado ao excluir grupo"));
        assertFalse(grupo.isAtivo());
        verify(exercicioRepo).findByGrupo_Id(grupoId);
        verify(exercicioRepo).saveAll(exercicios);
        verify(grupoRepo).findById(grupoId);
        verify(grupoRepo).save(grupo);
    }

    @Test
    void excluirGrupoComExercicios_erroGrupoNaoEncontrado() {
        when(exercicioRepo.findByGrupo_Id(grupoId)).thenReturn(new ArrayList<>());
        when(grupoRepo.findById(grupoId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.excluirGrupoComExercicios(grupoId));
        verify(grupoRepo).findById(grupoId);
    }
}
