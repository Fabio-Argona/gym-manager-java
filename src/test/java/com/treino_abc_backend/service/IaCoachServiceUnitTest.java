package com.treino_abc_backend.service;

import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.entity.IaMensagem;
import com.treino_abc_backend.entity.TreinoGrupo;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.repository.IaMensagemRepository;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import com.treino_abc_backend.dto.IaRespostaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IaCoachServiceUnitTest {
    @Mock
    private AlunoRepository alunoRepository;
    @Mock
    private TreinoGrupoRepository treinoGrupoRepository;
    @Mock
    private IaMensagemRepository iaMensagemRepository;

    @InjectMocks
    private IaCoachService iaCoachService;

    private UUID alunoId;
    private Aluno aluno;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        alunoId = UUID.randomUUID();
        aluno = new Aluno();
        aluno.setId(alunoId);
        aluno.setNome("João Silva");
    }

    @Test
    void quandoAlunoNaoExiste_deveLancarResponseStatusException() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> iaCoachService.verificarInicioAluno(alunoId));
        verify(alunoRepository, times(1)).findById(alunoId);
        verifyNoInteractions(treinoGrupoRepository);
        verifyNoInteractions(iaMensagemRepository);
    }

    @Test
    void quandoAlunoSemTreinosAtivos_deveRetornarMensagemEOpcoesESalvarMensagem() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        when(treinoGrupoRepository.findByAluno_IdAndAtivoTrue(alunoId)).thenReturn(Collections.emptyList());

        IaRespostaDTO resposta = iaCoachService.verificarInicioAluno(alunoId);

        assertNotNull(resposta);
        assertTrue(resposta.getResposta().contains("Olá, João"));
        assertTrue(resposta.getOpcoes().contains("Criar treino e exercícios"));
        assertTrue(resposta.getOpcoes().contains("Perguntar algo sobre o meu treino"));
        assertTrue(resposta.isSemTreinos());
        verify(alunoRepository, times(1)).findById(alunoId);
        verify(treinoGrupoRepository, times(1)).findByAluno_IdAndAtivoTrue(alunoId);
        verify(iaMensagemRepository, times(1)).save(any(IaMensagem.class));
    }

    @Test
    void quandoAlunoComTreinosAtivos_deveRetornarNullENaoSalvarMensagem() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        TreinoGrupo grupo = new TreinoGrupo();
        when(treinoGrupoRepository.findByAluno_IdAndAtivoTrue(alunoId)).thenReturn(List.of(grupo));

        IaRespostaDTO resposta = iaCoachService.verificarInicioAluno(alunoId);

        assertNull(resposta);
        verify(alunoRepository, times(1)).findById(alunoId);
        verify(treinoGrupoRepository, times(1)).findByAluno_IdAndAtivoTrue(alunoId);
        verifyNoInteractions(iaMensagemRepository);
    }
}
