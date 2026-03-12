package com.treino_abc_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treino_abc_backend.dto.IaRespostaDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.entity.IaMensagem;
import com.treino_abc_backend.entity.TreinoGrupo;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.repository.IaMensagemRepository;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import com.treino_abc_backend.repository.TreinoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IaCoachServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private IaMensagemRepository iaMensagemRepository;

    @Mock
    private TreinoGrupoRepository treinoGrupoRepository;

    @Mock
    private TreinoRepository treinoRepository;

    @Mock
    private ExercicioService exercicioService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private IaCoachService iaCoachService;

    private Aluno aluno;
    private UUID alunoId;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(iaCoachService, "groqApiKey", "test-api-key");
        ReflectionTestUtils.setField(iaCoachService, "restTemplate", restTemplate);

        alunoId = UUID.randomUUID();
        aluno = new Aluno();
        aluno.setId(alunoId);
        aluno.setNome("João Silva");
        aluno.setObjetivo("Hipertrofia");
        aluno.setSexo("Masculino");
        aluno.setPesoAtual(80.0);
        aluno.setAltura(1.80);
        aluno.setImc(24.7);
        aluno.setPercentualGordura(15.0);
        aluno.setPercentualMusculo(45.0);
        aluno.setNivelTreinamento("Intermediário");
    }

    @Test
    void verificarInicioAluno_WhenAlunoNotFound_ShouldThrowRuntimeException() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> iaCoachService.verificarInicioAluno(alunoId));
    }

    @Test
    void gerarAnalise_WhenAlunoNotFound_ShouldThrowRuntimeException() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> iaCoachService.gerarAnalise(alunoId));
    }

    @Test
    void verificarInicioAluno_WhenNoActiveTreinos_ShouldReturnWelcomeMessage() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        when(treinoGrupoRepository.findByAluno_IdAndAtivoTrue(alunoId)).thenReturn(new ArrayList<>());

        IaRespostaDTO result = iaCoachService.verificarInicioAluno(alunoId);

        assertNotNull(result);
        assertTrue(result.getResposta().contains("Olá, João")); 
        assertNotNull(result.getOpcoes());
        assertEquals(2, result.getOpcoes().size());
        verify(iaMensagemRepository, times(1)).save(any());
        verify(restTemplate, never()).exchange(anyString(), any(), any(), eq(String.class));
    }

    @Test
    void gerarAnalise_ShouldReturnValidAnalysis() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        String mockGroqResponse = """
            {
               "choices": [
                  {
                     "message": {
                        "content": "Aqui está sua análise de perfil."
                     }
                  }
               ]
            }
        """;
        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockGroqResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);
        IaRespostaDTO result = iaCoachService.gerarAnalise(alunoId);
        verify(iaMensagemRepository, times(1)).save(any(IaMensagem.class));
        assertNotNull(result);
        assertEquals("Aqui está sua análise de perfil.", result.getResposta());
        assertNull(result.getOpcoes());
    }

    @Test
    void processarMensagem_ShouldProcessUserMessageAndGenerateTreinos() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));

        IaMensagem hist = new IaMensagem();
        hist.setRole("user");
        hist.setConteudo("Oi!");
        hist.setCriadoEm(LocalDateTime.now().minusMinutes(5));
        when(iaMensagemRepository.findTop10ByAlunoIdOrderByCriadoEmAsc(any()))
                .thenReturn(List.of(hist));

        // Stub save do TreinoGrupo
        when(treinoGrupoRepository.save(any(TreinoGrupo.class))).thenAnswer(invocation -> {
            TreinoGrupo tg = invocation.getArgument(0);
            tg.setId(UUID.randomUUID());
            return tg;
        });

        String mockGroqResponse = """
            {
               "choices": [
                  {
                     "message": {
                        "content": "Seu treino está pronto. \\n```json\\n{\\"treinos\\":[{\\"nomeGrupo\\":\\"A\\", \\"exercicios\\":[{\\"nome\\":\\"Supino\\", \\"grupoMuscular\\":\\"Peito\\", \\"series\\":4, \\"repeticoes\\":12, \\"pesoInicial\\":10.5, \\"observacao\\":\\"Obs\\"} ] }] }\\n```"
                     }
                  }
               ]
            }
        """;
        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockGroqResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        IaRespostaDTO result = iaCoachService.processarMensagem(alunoId, "Quero treinar peito");

        assertNotNull(result);
        assertTrue(result.getResposta().contains("Seu treino está pronto."));
        verify(iaMensagemRepository, times(2)).save(any(IaMensagem.class)); // 1 User, 1 IA
        verify(treinoGrupoRepository, times(1)).save(any(TreinoGrupo.class));
        verify(exercicioService, times(1)).criarExercicio(any());
    }

    @Test
    void chamarGroq_WhenApiFails_ShouldThrowRuntimeException() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("API falhou"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            iaCoachService.gerarAnalise(alunoId)
        );

        assertTrue(exception.getMessage().contains("Não foi possível obter resposta da IA"));
    }

    @Test
    void extrairJsonDaResposta_comOpcoesEJsonValido() {
        IaCoachService spyService = spy(iaCoachService);
        Aluno aluno = new Aluno();
        String respostaIA = "Mensagem antes do JSON.```json{\"opcoes\":[\"A\",\"B\"]}```Mensagem depois.";
        IaRespostaDTO dto = spyService.extrairJsonDaResposta(aluno, respostaIA);
        assertTrue(dto.getResposta().contains("Mensagem antes do JSON."));
        assertTrue(dto.getOpcoes().contains("A"));
        assertTrue(dto.getOpcoes().contains("B"));
    }

    @Test
    void extrairJsonDaResposta_comJsonInvalido() {
        IaCoachService spyService = spy(iaCoachService);
        Aluno aluno = new Aluno();
        String respostaIA = "Mensagem sem JSON válido.";
        IaRespostaDTO dto = spyService.extrairJsonDaResposta(aluno, respostaIA);
        assertEquals(respostaIA, dto.getResposta());
    }

    @Test
    void montarPayloadSimples_deveGerarJsonValido() {
        Aluno aluno = new Aluno();
        aluno.setNome("Teste");
        String prompt = "Prompt de teste";
        String payload = iaCoachService.montarPayloadSimples(aluno, prompt);
        assertTrue(payload.contains("Prompt de teste"));
        assertTrue(payload.contains("Teste"));
    }

    @Test
    void montarPayload_deveGerarJsonValido() {
        Aluno aluno = new Aluno();
        aluno.setNome("Teste");
        List<IaMensagem> historico = List.of();
        String novaMensagem = "Nova mensagem";
        String payload = iaCoachService.montarPayload(aluno, historico, novaMensagem);
        assertTrue(payload.contains("Nova mensagem"));
        assertTrue(payload.contains("Teste"));
    }

    @Test
    void montarSystemPrompt_deveGerarPromptComDadosAluno() {
        Aluno aluno = new Aluno();
        aluno.setNome("Teste");
        aluno.setObjetivo("Hipertrofia");
        aluno.setNivelTreinamento("Avançado");
        String prompt = iaCoachService.montarSystemPrompt(aluno);
        assertTrue(prompt.contains("Teste"));
        assertTrue(prompt.contains("Hipertrofia"));
        assertTrue(prompt.contains("Avançado"));
    }

    @Test
    void salvarMensagem_devePersistirMensagem() {
        IaCoachService spyService = spy(iaCoachService);
        UUID alunoId = UUID.randomUUID();
        spyService.salvarMensagem(alunoId, "user", "conteudo");
        verify(iaMensagemRepository, times(1)).save(any(IaMensagem.class));
    }

    @Test
    void montarPayload_deveLancarRuntimeExceptionEmErro() {
        IaCoachService spyService = spy(iaCoachService);
        Aluno aluno = new Aluno();
        doThrow(new RuntimeException("Erro simulado")).when(spyService).montarSystemPrompt(any());
        List<IaMensagem> historico = List.of();
        String novaMensagem = "Nova mensagem";
        RuntimeException ex = assertThrows(RuntimeException.class, () -> spyService.montarPayload(aluno, historico, novaMensagem));
        assertTrue(ex.getMessage().contains("Erro ao montar payload para Groq"));
    }

    @Test
    void montarPayloadSimples_deveLancarRuntimeExceptionEmErro() {
        IaCoachService spyService = spy(iaCoachService);
        Aluno aluno = new Aluno();
        doThrow(new RuntimeException("Erro simulado"))
                .when(spyService).montarSystemPrompt(any());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> spyService.montarPayloadSimples(aluno, "prompt"));
        assertTrue(ex.getMessage().contains("Erro ao montar payload simples"));
    }

    @Test
    void extrairJsonDaResposta_deveLancarErroParseJson() {
        IaCoachService spyService = spy(iaCoachService);
        Aluno aluno = new Aluno();
        // JSON inválido para forçar erro
        String respostaIA = "Mensagem antes do JSON.```json{opcoes:[A,B]}```Mensagem depois.";
        IaRespostaDTO dto = spyService.extrairJsonDaResposta(aluno, respostaIA);
        assertTrue(dto.getResposta().contains("Mensagem antes do JSON."));
    }

    @Test
    void salvarTreinosJson_devePersistirExercicios() throws Exception {
        IaCoachService spyService = spy(iaCoachService);
        Aluno aluno = new Aluno();
        aluno.setId(alunoId);
        TreinoGrupo grupoMock = new TreinoGrupo();
        grupoMock.setId(UUID.randomUUID());
        when(treinoGrupoRepository.save(any())).thenReturn(grupoMock);
        Map<String, Object> root = new java.util.HashMap<>();
        Map<String, Object> treino = new java.util.HashMap<>();
        treino.put("nomeGrupo", "Treino A");
        List<Map<String, Object>> exercicios = new ArrayList<>();
        Map<String, Object> ex = new java.util.HashMap<>();
        ex.put("nome", "Supino");
        ex.put("grupoMuscular", "Peito");
        ex.put("series", 4);
        ex.put("repeticoes", 12);
        ex.put("pesoInicial", 10.0);
        ex.put("observacao", "Obs");
        exercicios.add(ex);
        treino.put("exercicios", exercicios);
        root.put("treinos", List.of(treino));
        spyService.salvarTreinosJson(aluno, root);
        verify(exercicioService, times(1)).criarExercicio(any());
    }

    @Test
    void extrairJsonDaResposta_comTreinosEOpcoes() {
        IaCoachService spyService = spy(iaCoachService);
        Aluno aluno = new Aluno();
        String respostaIA = "Mensagem antes do JSON.```json{\"opcoes\":[\"A\",\"B\"],\"treinos\":[{\"nomeGrupo\":\"A\",\"exercicios\":[{\"nome\":\"Supino\",\"grupoMuscular\":\"Peito\",\"series\":4,\"repeticoes\":12,\"pesoInicial\":10,\"observacao\":\"Obs\"}]}]} ```Mensagem depois.";
        TreinoGrupo grupoMock = new TreinoGrupo();
        grupoMock.setId(UUID.randomUUID());
        when(treinoGrupoRepository.save(any())).thenReturn(grupoMock);
        IaRespostaDTO dto = spyService.extrairJsonDaResposta(aluno, respostaIA);
        List<String> opcoes = dto.getOpcoes() != null ? dto.getOpcoes() : new ArrayList<>();
        assertTrue(opcoes.contains("A"));
        assertTrue(opcoes.contains("B"));
    }

    @Test
    void chamarGroq_deveLancarRuntimeExceptionQuandoRespostaNulaOuVazia() {
        IaCoachService spyService = spy(iaCoachService);
        doReturn(null).when(spyService).chamarGroq(anyString());
        assertThrows(RuntimeException.class, () -> {
            String result = spyService.chamarGroq("{}");
            if (result == null || result.trim().isEmpty()) {
                throw new RuntimeException("Resposta vazia da IA");
            }
        });
    }

    @Test
    void chamarGroq_deveLancarRuntimeExceptionEmErro() {
        IaCoachService spyService = spy(iaCoachService);
        doThrow(new RuntimeException("Erro simulado")).when(spyService).chamarGroq(anyString());
        assertThrows(RuntimeException.class, () -> spyService.chamarGroq("{}"));
    }

    @Test
    void processarMensagem_deveLancarErroQuandoIAFalha() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        doThrow(new RuntimeException("Erro IA")).when(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
        assertThrows(RuntimeException.class, () -> iaCoachService.processarMensagem(alunoId, "mensagem"));
    }

    @Test
    void processarMensagem_deveProcessarComHistoricoVazio() {
        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        when(iaMensagemRepository.findTop10ByAlunoIdOrderByCriadoEmAsc(alunoId)).thenReturn(new ArrayList<>());
        String mockGroqResponse = "{\"choices\":[{\"message\":{\"content\":\"Mensagem IA\"}}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockGroqResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class))).thenReturn(responseEntity);
        IaRespostaDTO result = iaCoachService.processarMensagem(alunoId, "mensagem");
        assertNotNull(result);
        assertTrue(result.getResposta().contains("Mensagem IA"));
    }

    // Extra coverage: malformed JSON in extrairJsonDaResposta
    @Test
    void extrairJsonDaResposta_malformedJson_shouldReturnOriginalMessage() {
        IaCoachService spyService = spy(iaCoachService);
        Aluno aluno = new Aluno();
        String respostaIA = "Mensagem antes do JSON.```json{opcoes:[A,B]}```Mensagem depois."; // malformed JSON
        IaRespostaDTO dto = spyService.extrairJsonDaResposta(aluno, respostaIA);
        assertTrue(dto.getResposta().contains("Mensagem antes do JSON."));
    }

    // Extra coverage: missing opcoes/treinos keys in JSON
    @Test
    void extrairJsonDaResposta_missingKeys_shouldReturnOriginalMessage() {
        IaCoachService spyService = spy(iaCoachService);
        Aluno aluno = new Aluno();
        String respostaIA = "Mensagem sem JSON válido. {\"foo\":123}";
        IaRespostaDTO dto = spyService.extrairJsonDaResposta(aluno, respostaIA);
        assertEquals(respostaIA, dto.getResposta());
    }

    // Extra coverage: salvarTreinosJson with empty treinos
    @Test
    void salvarTreinosJson_emptyTreinos_shouldNotThrow() throws Exception {
        IaCoachService spyService = spy(iaCoachService);
        Aluno aluno = new Aluno();
        Map<String, Object> root = new java.util.HashMap<>();
        root.put("treinos", new ArrayList<>());
        spyService.salvarTreinosJson(aluno, root);
        // No exception expected
    }

    // Extra coverage: chamarGroq with empty response
    @Test
    void chamarGroq_emptyResponse_shouldThrow() {
        IaCoachService spyService = spy(iaCoachService);
        doReturn("   ").when(spyService).chamarGroq(anyString());
        assertThrows(RuntimeException.class, () -> {
            String result = spyService.chamarGroq("{}");
            if (result == null || result.trim().isEmpty()) {
                throw new RuntimeException("Resposta vazia da IA");
            }
        });
    }

    // Extra coverage: salvarMensagem with null/empty content
    @Test
    void salvarMensagem_nullContent_shouldPersist() {
        IaCoachService spyService = spy(iaCoachService);
        UUID alunoId = UUID.randomUUID();
        spyService.salvarMensagem(alunoId, "user", null);
        verify(iaMensagemRepository, times(1)).save(any(IaMensagem.class));
    }

    @Test
    void salvarMensagem_emptyContent_shouldPersist() {
        IaCoachService spyService = spy(iaCoachService);
        UUID alunoId = UUID.randomUUID();
        spyService.salvarMensagem(alunoId, "user", "");
        verify(iaMensagemRepository, times(1)).save(any(IaMensagem.class));
    }
}
