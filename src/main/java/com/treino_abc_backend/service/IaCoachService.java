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
import java.util.Map;
import java.util.UUID;

/**
 * Serviço responsável por gerenciar a interação com a IA Coach (Gemini).
 * Mantém histórico de mensagens personalizado por aluno.
 */
@Service
public class IaCoachService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

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
     * Processa a mensagem do aluno e retorna a resposta da IA.
     *
     * @param alunoId  ID do aluno autenticado
     * @param mensagem Texto enviado pelo aluno
     * @return Texto de resposta gerado pelo Gemini
     */
    public String processarMensagem(UUID alunoId, String mensagem) {
        // 1. Buscar dados do aluno para contextualizar a IA
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado: " + alunoId));

        // 2. Buscar histórico das últimas 10 mensagens (contexto da conversa)
        List<IaMensagem> historico = iaMensagemRepository
                .findTop10ByAlunoIdOrderByCriadoEmAsc(alunoId);

        // 3. Salvar a mensagem do usuário no histórico
        salvarMensagem(alunoId, "user", mensagem);

        // 4. Montar o payload para o Gemini
        String payload = montarPayload(aluno, historico, mensagem);

        // 5. Chamar API do Gemini
        String respostaIA = chamarGemini(payload);

        // 6. Salvar a resposta da IA no histórico
        salvarMensagem(alunoId, "model", respostaIA);

        return respostaIA;
    }

    /**
     * Monta o JSON do payload para a API Gemini com o system prompt personalizado
     * e o histórico de mensagens.
     */
    private String montarPayload(Aluno aluno, List<IaMensagem> historico, String novaMensagem) {
        try {
            // System instruction com dados do aluno
            String systemInstruction = montarSystemPrompt(aluno);

            // Construir array de "contents" com histórico + nova mensagem
            StringBuilder contentsBuilder = new StringBuilder("[");

            for (IaMensagem msg : historico) {
                contentsBuilder.append("{")
                        .append("\"role\":\"").append(msg.getRole()).append("\",")
                        .append("\"parts\":[{\"text\":").append(objectMapper.writeValueAsString(msg.getConteudo()))
                        .append("}]")
                        .append("},");
            }

            // Adicionar a nova mensagem do usuário
            contentsBuilder.append("{")
                    .append("\"role\":\"user\",")
                    .append("\"parts\":[{\"text\":").append(objectMapper.writeValueAsString(novaMensagem)).append("}]")
                    .append("}]");

            return "{"
                    + "\"system_instruction\":{\"parts\":[{\"text\":"
                    + objectMapper.writeValueAsString(systemInstruction)
                    + "}]},"
                    + "\"contents\":" + contentsBuilder
                    + "}";

        } catch (Exception e) {
            throw new RuntimeException("Erro ao montar payload para Gemini: " + e.getMessage(), e);
        }
    }

    /**
     * Cria o system prompt personalizado com os dados físicos e objetivos do aluno.
     */
    private String montarSystemPrompt(Aluno aluno) {
        StringBuilder sb = new StringBuilder();
        sb.append("Você é um coach de fitness pessoal especializado, integrado ao aplicativo Gym Manager. ");
        sb.append("Seja sempre motivador, claro e prático nas respostas em português do Brasil. ");
        sb.append("Responda de forma concisa mas completa.\n\n");
        sb.append("=== DADOS DO ALUNO ===\n");
        sb.append("Nome: ").append(aluno.getNome() != null ? aluno.getNome() : "Não informado").append("\n");

        if (aluno.getObjetivo() != null && !aluno.getObjetivo().isBlank()) {
            sb.append("Objetivo: ").append(aluno.getObjetivo()).append("\n");
        }
        if (aluno.getNivelTreinamento() != null && !aluno.getNivelTreinamento().isBlank()) {
            sb.append("Nível de treinamento: ").append(aluno.getNivelTreinamento()).append("\n");
        }
        if (aluno.getSexo() != null) {
            sb.append("Sexo: ").append(aluno.getSexo()).append("\n");
        }
        if (aluno.getPesoAtual() != null) {
            sb.append("Peso atual: ").append(aluno.getPesoAtual()).append(" kg\n");
        }
        if (aluno.getAltura() != null) {
            sb.append("Altura: ").append(aluno.getAltura()).append(" m\n");
        }
        if (aluno.getImc() != null) {
            sb.append("IMC: ").append(String.format("%.1f", aluno.getImc())).append("\n");
        }
        if (aluno.getPercentualGordura() != null) {
            sb.append("% Gordura: ").append(aluno.getPercentualGordura()).append("%\n");
        }
        if (aluno.getPercentualMusculo() != null) {
            sb.append("% Músculo: ").append(aluno.getPercentualMusculo()).append("%\n");
        }

        sb.append("=====================\n\n");
        sb.append("Use esses dados para personalizar suas respostas. ");
        sb.append("Responda apenas perguntas relacionadas a fitness, saúde, treino, nutrição e bem-estar.");

        return sb.toString();
    }

    /**
     * Faz a chamada HTTP para a API do Gemini e retorna o texto da resposta.
     */
    private String chamarGemini(String payloadJson) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(payloadJson, headers);

            String url = GEMINI_URL + geminiApiKey;

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            // Parsear resposta JSON do Gemini
            JsonNode root = objectMapper.readTree(response.getBody());
            String texto = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text")
                    .asText();

            if (texto == null || texto.isBlank()) {
                throw new RuntimeException("Resposta vazia do Gemini");
            }

            return texto;

        } catch (Exception e) {
            // Log e retorno de mensagem amigável em caso de erro
            System.err.println("[IaCoachService] Erro ao chamar Gemini: " + e.getMessage());
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
