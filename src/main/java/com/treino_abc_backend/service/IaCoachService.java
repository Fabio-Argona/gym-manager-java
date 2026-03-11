package com.treino_abc_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.entity.IaMensagem;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.repository.IaMensagemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

/**
 * Serviço responsável por gerenciar a interação com a IA Coach (Groq/Llama).
 * Usa a API do Groq (compatível com OpenAI) — gratuita e com 14.400 req/dia.
 */
@Service
public class IaCoachService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String GROQ_MODEL = "llama-3.3-70b-versatile";

    private final AlunoRepository alunoRepository;
    private final IaMensagemRepository iaMensagemRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public IaCoachService(AlunoRepository alunoRepository,
            IaMensagemRepository iaMensagemRepository) {
        this.alunoRepository = alunoRepository;
        this.iaMensagemRepository = iaMensagemRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Gera uma análise automática do perfil do aluno ao abrir a aba IA Coach.
     */
    public String gerarAnalise(UUID alunoId) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado: " + alunoId));

        String promptAnalise = "Faça uma análise motivadora e personalizada do perfil deste aluno. "
                + "Destaque os pontos positivos, sugira foco de treino com base no objetivo e nível, "
                + "e dê uma dica prática para a semana. Seja direto e use no máximo 5 parágrafos curtos.";

        String payload = montarPayloadSimples(aluno, promptAnalise);
        String resposta = chamarGroq(payload);
        salvarMensagem(alunoId, "model", resposta);
        return resposta;
    }

    /**
     * Processa a mensagem do aluno e retorna a resposta da IA.
     */
    public String processarMensagem(UUID alunoId, String mensagem) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado: " + alunoId));

        List<IaMensagem> historico = iaMensagemRepository
                .findTop10ByAlunoIdOrderByCriadoEmAsc(alunoId);

        salvarMensagem(alunoId, "user", mensagem);

        String payload = montarPayload(aluno, historico, mensagem);
        String respostaIA = chamarGroq(payload);
        salvarMensagem(alunoId, "model", respostaIA);
        return respostaIA;
    }

    /**
     * Monta o payload no formato OpenAI/Groq com histórico de mensagens.
     * {"model":"...","messages":[{"role":"system","content":"..."},{"role":"user","content":"..."},...]}
     */
    private String montarPayload(Aluno aluno, List<IaMensagem> historico, String novaMensagem) {
        try {
            String systemPrompt = montarSystemPrompt(aluno);
            StringBuilder messages = new StringBuilder("[");

            // System message
            messages.append("{\"role\":\"system\",\"content\":")
                    .append(objectMapper.writeValueAsString(systemPrompt))
                    .append("}");

            // Histórico de mensagens (user/assistant)
            for (IaMensagem msg : historico) {
                // Groq usa "assistant" em vez de "model"
                String role = "model".equals(msg.getRole()) ? "assistant" : "user";
                messages.append(",{\"role\":\"").append(role).append("\",\"content\":")
                        .append(objectMapper.writeValueAsString(msg.getConteudo()))
                        .append("}");
            }

            // Nova mensagem do usuário
            messages.append(",{\"role\":\"user\",\"content\":")
                    .append(objectMapper.writeValueAsString(novaMensagem))
                    .append("}]");

            return "{\"model\":\"" + GROQ_MODEL + "\","
                    + "\"messages\":" + messages + ","
                    + "\"temperature\":0.7,"
                    + "\"max_tokens\":1024}";

        } catch (Exception e) {
            throw new RuntimeException("Erro ao montar payload para Groq: " + e.getMessage(), e);
        }
    }

    /**
     * Monta payload simples (sem histórico) para análise inicial do perfil.
     */
    private String montarPayloadSimples(Aluno aluno, String prompt) {
        try {
            String systemPrompt = montarSystemPrompt(aluno);
            return "{\"model\":\"" + GROQ_MODEL + "\","
                    + "\"messages\":["
                    + "{\"role\":\"system\",\"content\":" + objectMapper.writeValueAsString(systemPrompt) + "},"
                    + "{\"role\":\"user\",\"content\":" + objectMapper.writeValueAsString(prompt) + "}"
                    + "],"
                    + "\"temperature\":0.7,"
                    + "\"max_tokens\":1024}";
        } catch (Exception e) {
            throw new RuntimeException("Erro ao montar payload simples: " + e.getMessage(), e);
        }
    }

    /**
     * Cria o system prompt personalizado com os dados do aluno.
     */
    private String montarSystemPrompt(Aluno aluno) {
        StringBuilder sb = new StringBuilder();
        sb.append("Você é um coach de fitness pessoal especializado, integrado ao aplicativo Gym Manager. ");
        sb.append("Seja sempre motivador, claro e prático nas respostas em português do Brasil. ");
        sb.append("Responda de forma concisa mas completa.\n\n");
        sb.append("=== DADOS DO ALUNO ===\n");
        sb.append("Nome: ").append(aluno.getNome() != null ? aluno.getNome() : "Não informado").append("\n");

        if (aluno.getObjetivo() != null && !aluno.getObjetivo().isBlank())
            sb.append("Objetivo: ").append(aluno.getObjetivo()).append("\n");
        if (aluno.getNivelTreinamento() != null && !aluno.getNivelTreinamento().isBlank())
            sb.append("Nível: ").append(aluno.getNivelTreinamento()).append("\n");
        if (aluno.getSexo() != null)
            sb.append("Sexo: ").append(aluno.getSexo()).append("\n");
        if (aluno.getPesoAtual() != null)
            sb.append("Peso: ").append(aluno.getPesoAtual()).append(" kg\n");
        if (aluno.getAltura() != null)
            sb.append("Altura: ").append(aluno.getAltura()).append(" m\n");
        if (aluno.getImc() != null)
            sb.append("IMC: ").append(String.format("%.1f", aluno.getImc())).append("\n");
        if (aluno.getPercentualGordura() != null)
            sb.append("% Gordura: ").append(aluno.getPercentualGordura()).append("%\n");
        if (aluno.getPercentualMusculo() != null)
            sb.append("% Músculo: ").append(aluno.getPercentualMusculo()).append("%\n");

        sb.append("=====================\n\n");
        sb.append("Use esses dados para personalizar suas respostas. ");
        sb.append("Responda apenas perguntas relacionadas a fitness, saúde, treino, nutrição e bem-estar.");
        return sb.toString();
    }

    /**
     * Faz a chamada HTTP para a API do Groq e retorna o texto da resposta.
     * Formato de resposta: choices[0].message.content
     */
    private String chamarGroq(String payloadJson) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(groqApiKey);

            HttpEntity<String> requestEntity = new HttpEntity<>(payloadJson, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    GROQ_URL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            // Parsear resposta: choices[0].message.content
            JsonNode root = objectMapper.readTree(response.getBody());
            String texto = root
                    .path("choices").get(0)
                    .path("message")
                    .path("content")
                    .asText();

            if (texto == null || texto.isBlank()) {
                throw new RuntimeException("Resposta vazia da IA");
            }

            return texto;

        } catch (Exception e) {
            System.err.println("[IaCoachService] Erro ao chamar Groq: " + e.getMessage());
            throw new RuntimeException("Não foi possível obter resposta da IA. Tente novamente.", e);
        }
    }

    /**
     * Persiste uma mensagem (user ou model) no histórico do banco.
     */
    private void salvarMensagem(UUID alunoId, String role, String conteudo) {
        IaMensagem msg = new IaMensagem();
        msg.setAlunoId(alunoId);
        msg.setRole(role);
        msg.setConteudo(conteudo);
        iaMensagemRepository.save(msg);
    }
}
