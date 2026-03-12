package com.treino_abc_backend.service;

import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.entity.IaMensagem;
import com.treino_abc_backend.dto.IaRespostaDTO;
import com.treino_abc_backend.entity.TreinoGrupo;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.repository.IaMensagemRepository;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IaCoachServiceFullTest {
    @Mock
    private AlunoRepository alunoRepository;
    @Mock
    private TreinoGrupoRepository treinoGrupoRepository;
    @Mock
    private IaMensagemRepository iaMensagemRepository;
    @Mock
    private RestTemplate restTemplate;

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
    void verificarInicioAluno_alunoNaoExiste_lancaResponseStatusException() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> iaCoachService.verificarInicioAluno(alunoId));
    }

    @Test
    void verificarInicioAluno_semTreinosAtivos_retornaMensagemESalva() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        when(treinoGrupoRepository.findByAluno_IdAndAtivoTrue(alunoId)).thenReturn(Collections.emptyList());
        IaRespostaDTO resposta = iaCoachService.verificarInicioAluno(alunoId);
        assertNotNull(resposta);
        assertTrue(resposta.isSemTreinos());
        assertTrue(resposta.getOpcoes().contains("Criar treino e exercícios"));
        verify(iaMensagemRepository, times(1)).save(any(IaMensagem.class));
    }

    @Test
    void verificarInicioAluno_comTreinosAtivos_retornaNullENaoSalva() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        TreinoGrupo grupo = new TreinoGrupo();
        when(treinoGrupoRepository.findByAluno_IdAndAtivoTrue(alunoId)).thenReturn(List.of(grupo));
        IaRespostaDTO resposta = iaCoachService.verificarInicioAluno(alunoId);
        assertNull(resposta);
        verifyNoInteractions(iaMensagemRepository);
    }

    @Test
    void gerarAnalise_alunoNaoExiste_lancaRuntimeException() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> iaCoachService.gerarAnalise(alunoId));
    }

    @Test
    void gerarAnalise_alunoExiste_retornaAnaliseESalva() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        // Mockar resposta da IA
        // O método chamarGroq e extrairJsonDaResposta devem ser mockados se forem públicos ou injetados
        // Aqui só validamos a chamada ao save
        IaCoachService spyService = spy(iaCoachService);
        doReturn("resposta json").when(spyService).chamarGroq(anyString());
        doReturn(new IaRespostaDTO("Análise", false, null)).when(spyService).extrairJsonDaResposta(any(), any());
        IaRespostaDTO resposta = spyService.gerarAnalise(alunoId);
        assertNotNull(resposta);
        assertEquals("Análise", resposta.getResposta());
        verify(spyService, times(1)).salvarMensagem(eq(alunoId), eq("model"), eq("Análise"));
    }

    @Test
    void processarMensagem_alunoNaoExiste_lancaRuntimeException() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> iaCoachService.processarMensagem(alunoId, "mensagem"));
    }

    @Test
    void processarMensagem_alunoExiste_retornaRespostaESalva() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        when(iaMensagemRepository.findTop10ByAlunoIdOrderByCriadoEmAsc(alunoId)).thenReturn(Collections.emptyList());
        IaCoachService spyService = spy(iaCoachService);
        doReturn("resposta json").when(spyService).chamarGroq(anyString());
        doReturn(new IaRespostaDTO("Resposta IA", false, null)).when(spyService).extrairJsonDaResposta(any(), any());
        IaRespostaDTO resposta = spyService.processarMensagem(alunoId, "mensagem");
        assertNotNull(resposta);
        assertEquals("Resposta IA", resposta.getResposta());
        verify(spyService, times(1)).salvarMensagem(eq(alunoId), eq("user"), eq("mensagem"));
        verify(spyService, times(1)).salvarMensagem(eq(alunoId), eq("model"), eq("Resposta IA"));
    }
}
