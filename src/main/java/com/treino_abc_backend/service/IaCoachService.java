package com.treino_abc_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.treino_abc_backend.dto.IaRespostaDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.entity.IaMensagem;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.repository.IaMensagemRepository;
import com.treino_abc_backend.repository.TreinoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.ArrayList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import com.treino_abc_backend.service.ExercicioService;
import com.treino_abc_backend.dto.ExercicioDTO;
import com.treino_abc_backend.entity.TreinoGrupo;

/**
 * Serviço responsável por gerenciar a interação com a IA Coach (Groq/Llama).
 * Usa a API do Groq (compatível com OpenAI) — gratuita e com 14.400 req/dia.
 */
@Service
public class IaCoachService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String GROQ_MODEL = "llama-3.1-8b-instant";

    private final AlunoRepository alunoRepository;
    private final IaMensagemRepository iaMensagemRepository;
    private final TreinoGrupoRepository treinoGrupoRepository;
    private final ExercicioService exercicioService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public IaCoachService(AlunoRepository alunoRepository,
            IaMensagemRepository iaMensagemRepository,
            TreinoRepository treinoRepository,
            TreinoGrupoRepository treinoGrupoRepository,
            ExercicioService exercicioService) {
        this.alunoRepository = alunoRepository;
        this.iaMensagemRepository = iaMensagemRepository;
        this.treinoGrupoRepository = treinoGrupoRepository;
        this.exercicioService = exercicioService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Verifica se o aluno possui treinos e exercícios cadastrados.
     * Caso não possua, retorna uma pergunta motivadora com opções de ação
     * para o Flutter exibir botões interativos de escolha.
     * Caso já possua treinos, retorna null e segue o fluxo normal de análise.
     */
    public IaRespostaDTO verificarInicioAluno(UUID alunoId) {
        Aluno aluno = alunoRepository.findById(alunoId)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND,
                "Aluno não encontrado: " + alunoId));

        boolean possuiTreinos = !treinoGrupoRepository.findByAluno_IdAndAtivoTrue(alunoId).isEmpty();

        if (!possuiTreinos) {
            String nomeAluno = aluno.getNome() != null ? aluno.getNome().split(" ")[0] : "";
            String pergunta = "Olá" + (nomeAluno.isBlank() ? "" : ", " + nomeAluno) + "! 👋\n\n"
                + "Percebi que você ainda não possui treinos cadastrados. "
                + "Que tal começarmos agora? 💪\n\n"
                + "O que você gostaria de fazer?";

            List<String> opcoes = Arrays.asList(
                "Criar treino e exercícios",
                "Perguntar algo sobre o meu treino");

            // Salva mensagem no histórico
            salvarMensagem(alunoId, "model", pergunta);

            return new IaRespostaDTO(pergunta, true, opcoes);
        }

        // Aluno já possui treinos — retorna null para o controller seguir com a análise
        // normal
        return null;
    }

    /**
     * Gera uma análise automática do perfil do aluno ao abrir a aba IA Coach.
     */
    public IaRespostaDTO gerarAnalise(UUID alunoId) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado: " + alunoId));

        String promptAnalise = "Faça uma análise motivadora e personalizada do perfil deste aluno. "
                + "Destaque os pontos positivos, sugira foco de treino com base no objetivo e nível, "
                + "e dê uma dica prática para a semana. Seja direto e use no máximo 5 parágrafos curtos.";

        String payload = montarPayloadSimples(aluno, promptAnalise);
        String resposta = chamarGroq(payload);
            if (resposta == null || resposta.trim().isEmpty()) {
                throw new RuntimeException("Resposta vazia da IA");
            }

        IaRespostaDTO respostaDTO = extrairJsonDaResposta(aluno, resposta);

        salvarMensagem(alunoId, "model", respostaDTO.getResposta());

        return respostaDTO;
    }

    /**
     * Processa a mensagem do aluno e retorna a resposta da IA.
     */
    public IaRespostaDTO processarMensagem(UUID alunoId, String mensagem) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado: " + alunoId));

        List<IaMensagem> historico = iaMensagemRepository
                .findTop10ByAlunoIdOrderByCriadoEmAsc(alunoId);

        salvarMensagem(alunoId, "user", mensagem);

        String payload = montarPayload(aluno, historico, mensagem);
        String respostaIA = chamarGroq(payload);

        // Extrai o JSON e salva os treinos no banco de dados se for o caso
        IaRespostaDTO respostaDTO = extrairJsonDaResposta(aluno, respostaIA);

        salvarMensagem(alunoId, "model", respostaDTO.getResposta());
        return respostaDTO;
    }

    /**
     * Monta o payload no formato OpenAI/Groq com histórico de mensagens.
     * {"model":"...","messages":[{"role":"system","content":"..."},{"role":"user","content":"..."},...]}
     */
    String montarPayload(Aluno aluno, List<IaMensagem> historico, String novaMensagem) {
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
                    + "\"max_tokens\":8192}";

        } catch (Exception e) {
            throw new RuntimeException("Erro ao montar payload para Groq: " + e.getMessage(), e);
        }
    }

    /**
     * Monta payload simples (sem histórico) para análise inicial do perfil.
     */
    String montarPayloadSimples(Aluno aluno, String prompt) {
        try {
            String systemPrompt = montarSystemPrompt(aluno);
            return "{\"model\":\"" + GROQ_MODEL + "\","
                    + "\"messages\":["
                    + "{\"role\":\"system\",\"content\":" + objectMapper.writeValueAsString(systemPrompt) + "},"
                    + "{\"role\":\"user\",\"content\":" + objectMapper.writeValueAsString(prompt) + "}"
                    + "],"
                    + "\"temperature\":0.7,"
                    + "\"max_tokens\":8192}";
        } catch (Exception e) {
            throw new RuntimeException("Erro ao montar payload simples: " + e.getMessage(), e);
        }
    }

    /**
     * Cria o system prompt personalizado com os dados do aluno.
     */
    String montarSystemPrompt(Aluno aluno) {
        StringBuilder sb = new StringBuilder();
        sb.append("Você é um coach de fitness pessoal especializado, integrado ao aplicativo Gym Manager. ");
        sb.append("Seja sempre motivador, claro e prático nas respostas em português do Brasil. ");
        sb.append("Responda de forma concisa mas completa.\n\n");
        sb.append("=== DADOS DO ALUNO ===\n");
        sb.append("Nome: ").append(aluno.getNome() != null ? aluno.getNome() : "Não informado").append("\n");

        if (aluno.getDataNascimento() != null) {
            int idade = java.time.Period.between(aluno.getDataNascimento(), java.time.LocalDate.now()).getYears();
            sb.append("Idade: ").append(idade).append(" anos\n");
        }
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
        sb.append("Use esses dados para personalizar suas respostas.\n\n");
        sb.append("REGRAS IMPORTANTES PARA CRIAR TREINOS:\n");
        sb.append("- NUNCA crie um treino genérico imediatamente. ANTES de sugerir qualquer treino, você DEVE descobrir as informações abaixo (caso não estejam no perfil).\n");
        sb.append("- PARA NÃO SER REDUNDANTE: Não faça perguntas desnecessárias como 'você já treina'. Vá direto ao ponto.\n");
        sb.append("- FAÇA APENAS UMA PERGUNTA POR VEZ! Aguarde a resposta do usuário antes de seguir para a próxima.\n");
        sb.append("- Ordem das perguntas para criar o treino:\n");
        sb.append("  1. Qual o seu sexo biológico? (Apenas se não tiver no perfil)\n");
        sb.append("  2. Qual o seu objetivo principal? (Emagrecimento, Hipertrofia, etc - Apenas se não tiver no perfil)\n");
        sb.append("  3. Qual divisão de treino você prefere? (ABC, ABCDE, etc)\n");
        sb.append("- É OBRIGATÓRIO fornecer opções de respostas rápidas para QUALQUER pergunta que você fizer. Coloque as alternativas num BLOCO JSON VÁLIDO no final da mensagem no campo `opcoes`.\n");
        sb.append("  - Exemplo Objetivo: [\"Hipertrofia\", \"Emagrecimento\", \"Estética\", \"Saúde\"]\n");
        sb.append("ATENÇÃO MÁXIMA: É estritamente proibido imprimir as opções como texto na tela (ex: [opcoes]: [\"Sim\", \"Não\"]). Você deve OBRIGATORIAMENTE ocultar isso dentro de um bloco formatado com chaves {} e markdown de json!\n");
        sb.append("Exemplo exato do formato que você deve devolver no final da mensagem:\n");
        sb.append("```json\n");
        sb.append("{\n  \"opcoes\": [\"Masculino\", \"Feminino\"]\n}\n");
        sb.append("```\n\n");
        sb.append(
                "- Quando você for criar um roteiro de treino (APÓS fazer as perguntas para descobrir o perfil do usuário), A ÚNICA CONDIÇÃO INEGOCIÁVEL É: VOCÊ DEVERÁ GERAR EXATAMENTE 8 EXERCÍCIOS PARA CADA SESSÃO (por ex: 8 exercícios no Treino A, 8 no Treino B, etc). Essa regra de volume não pode ser quebrada em hipótese alguma. Divida bem a quantidade entre músculos principais e sinergistas.\n\n");
        sb.append(
                "- Se a escolha for Treino ABC, siga RIGOROSAMENTE a literatura: Treino A = Peito e Tríceps. Treino B = Costas e Bíceps. Treino C = Pernas e Ombro. Se for outra divisão, mantenha essa lógica base.\n\n");
        sb.append(
                "- Além disso, para a chave JSON \"grupoMuscular\", VOCÊ SÓ PODE USAR OS SEGUINTES NOMES EXATOS (são Enums do sistema da academia): \"Peito\", \"Costas\", \"Tríceps\", \"Bíceps\", \"Ombro\", \"Perna\", \"Abdômen\" ou \"Panturrilha\". NUNCA use Peitoral ou Dorsal ou Quadríceps, ou o app irá quebrar.\n\n");
        sb.append(
                "- Você MUST preencher o atributo \"observacao\" no JSON sugerido, preenchendo-o com uma técnica de execução para cada exercício. Não deixe o bloco vazio.\n\n");
        sb.append(
                "- O atributo JSON \"repeticoes\" e \"series\" DEVEM ser ÚNICA E EXCLUSIVAMENTE NÚMEROS INTEIROS (ex: 12). NUNCA envie faixas de texto como \"10-12\" ou o sistema irá quebrar ao converter.\n\n");
        sb.append(
                "- **MUITO IMPORTANTE:** No momento em que você GERAR A ROTINA DE TREINOS, você DEVE OBRIGATORIAMENTE incluir um BLOCO JSON no final da resposta com a estrutura. Como o JSON fica oculto para o usuário, NUNCA diga \"Aqui está a estrutura do seu treino em JSON\". Em vez disso, diga obrigatoriamente algo como: \"Os seus treinos foram gerados! Confira seus treinos na aba Treinos no rodapé do aplicativo!\"\n");
        sb.append("Siga exatamente o formato JSON (incluindo aspas, chaves e colchetes corretos):\n");
        sb.append("```json\n");
        sb.append("{\n");
        sb.append("  \"treinos\": [\n");
        sb.append("    {\n");
        sb.append("      \"nomeGrupo\": \"Treino A - Peito\",\n");
        sb.append("      \"exercicios\": [\n");
        sb.append("        {\n");
        sb.append("          \"nome\": \"Supino Reto\",\n");
        sb.append("          \"grupoMuscular\": \"Peito\",\n");
        sb.append("          \"series\": 4,\n");
        sb.append("          \"repeticoes\": 12,\n");
        sb.append("          \"pesoInicial\": 10,\n");
        sb.append("          \"observacao\": \"Mesa de supino com barra reta\"\n");
        sb.append("        },\n");
        sb.append(
                "        {  NUNCA pare em apenas 1 exercício! Você deve adicionar os 8 exercícios debaixo um do outro em VÁRIOS blocos como este até chegar em 8 na lista do Treino A, mais 8 blocos no Treino B, etc... }\n");
        sb.append("      ]\n");
        sb.append("    },\n");
        sb.append(
                "    {  Crie aqui o Treino B se o usuário escolher ABC, e seus respectivos 8 exercícios de costas... etc... }\n");
        sb.append("  ]\n");
        sb.append("}\n");
        sb.append("```\n\n");
        sb.append(
                "- Além disso, se você fizer alguma pergunta ao usuário (por exemplo, qual o objetivo, ou a quanto tempo treina) e quiser sugerir opções de respostas prontas, você também pode usar o bloco JSON para enviar um array de `opcoes`:\n");
        sb.append("```json\n");
        sb.append("{\n");
        sb.append("  \"opcoes\": [\"Hipertrofia\", \"Emagrecimento\", \"Manutenção\"]\n");
        sb.append("}\n");
        sb.append("```\n");
        sb.append(
                "Esses atributos ('treinos' e 'opcoes') podem vir no mesmo JSON ou sozinhos num bloco só no final do texto.\n\n");
        sb.append("Responda apenas a perguntas relacionadas a fitness, saúde, treino, nutrição e bem-estar.");
        return sb.toString();
    }

    /**
     * Intercepta o bloco de JSON do modelo e extrai/salva no DB do aluno, também
     * extraindo opções.
     */
    IaRespostaDTO extrairJsonDaResposta(Aluno aluno, String respostaIA) {
        String jsonStr = null;
        int startIndex = -1;
        int endIndex = -1;
        List<String> opcoesDetectadas = new ArrayList<>();

        // Procura pelo bloco JSON de forma mais resiliente (qualquer {...} no texto que
        // contenha opcoes ou treinos)
        int firstBrace = respostaIA.indexOf("{");
        int lastBrace = respostaIA.lastIndexOf("}");

        if (firstBrace != -1 && lastBrace != -1 && firstBrace < lastBrace) {
            String candidate = respostaIA.substring(firstBrace, lastBrace + 1);
            if (candidate.contains("\"opcoes\"") || candidate.contains("\"treinos\"")) {
                jsonStr = candidate;
                startIndex = firstBrace;
                endIndex = lastBrace + 1;

                // Expandir o apagador para remover as crases do markdown também, caso a IA
                // tenha embutido
                int markdownStart = respostaIA.lastIndexOf("```", firstBrace);
                if (markdownStart != -1 && markdownStart > firstBrace - 15) { // Check proximity
                    startIndex = markdownStart;
                }
                int markdownEnd = respostaIA.indexOf("```", lastBrace);
                if (markdownEnd != -1 && markdownEnd < lastBrace + 15) { // Check proximity
                    endIndex = markdownEnd + 3;
                }
            }
        }

        if (jsonStr != null) {
            try {
                Map<String, Object> root = objectMapper.readValue(jsonStr, new TypeReference<Map<String, Object>>() {
                });

                // Extrai as opções com segurança contra ClassCastException
                Object opcoesObj = root.get("opcoes");
                if (opcoesObj instanceof List) {
                    for (Object o : (List<?>) opcoesObj) {
                        if (o != null)
                            opcoesDetectadas.add(o.toString());
                    }
                }

                // Salva treinos, se estiver no JSON
                salvarTreinosJson(aluno, root);

                // Remove o bloco JSON da mensagem que será retornada para a tela
                String msgLimpa = respostaIA.substring(0, startIndex);
                if (endIndex < respostaIA.length()) { // concatena caso tenha algo depois do bloco
                    msgLimpa += respostaIA.substring(endIndex);
                }
                msgLimpa = msgLimpa.trim();

                IaRespostaDTO dto = new IaRespostaDTO(msgLimpa);
                if (!opcoesDetectadas.isEmpty()) {
                    dto.setOpcoes(opcoesDetectadas);
                }
                return dto;

            } catch (Exception e) {
                System.err.println("Erro ao parsear JSON da IA: " + e.getMessage());
                // Se houve erro de parsing, tenta extrair opções manualmente do texto
                if (respostaIA.contains("\"opcoes\"")) {
                    // Busca por um bloco de opções simples
                    String opcoesRegex = "\\[([^\\]]+)\\]";
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile(opcoesRegex);
                    java.util.regex.Matcher m = p.matcher(respostaIA);
                    while (m.find()) {
                        String[] opcoesArr = m.group(1).split(",");
                        for (String o : opcoesArr) {
                            String clean = o.replaceAll("[\" ]", "").trim();
                            if (!clean.isEmpty()) opcoesDetectadas.add(clean);
                        }
                    }
                }
            }
        } else {
            // Caso não tenha bloco JSON, tenta extrair opções manualmente do texto
            if (respostaIA.contains("\"opcoes\"")) {
                String opcoesRegex = "\\[([^\\]]+)\\]";
                java.util.regex.Pattern p = java.util.regex.Pattern.compile(opcoesRegex);
                java.util.regex.Matcher m = p.matcher(respostaIA);
                while (m.find()) {
                    String[] opcoesArr = m.group(1).split(",");
                    for (String o : opcoesArr) {
                        String clean = o.replaceAll("[\" ]", "").trim();
                        if (!clean.isEmpty()) opcoesDetectadas.add(clean);
                    }
                }
            }
        }

        IaRespostaDTO dto = new IaRespostaDTO(respostaIA);
        if (!opcoesDetectadas.isEmpty()) {
            dto.setOpcoes(opcoesDetectadas);
        }
        return dto;
    }

    /**
     * Faz parsing do JSON e insere Grupos (TreinoGrupo) e seus atributos no banco.
     */
    void salvarTreinosJson(Aluno aluno, Map<String, Object> root) throws Exception {
        List<Map<String, Object>> treinosData = (List<Map<String, Object>>) root.get("treinos");

        if (treinosData == null) {
            return; // não salvou com a chave correta
        }

        for (Map<String, Object> tData : treinosData) {
            String nomeGrupo = (String) tData.get("nomeGrupo");

            TreinoGrupo grupoNovo = new TreinoGrupo();
            grupoNovo.setNome(nomeGrupo);
            grupoNovo.setAluno(aluno);
            TreinoGrupo grupoSalvo = treinoGrupoRepository.save(grupoNovo);

            List<Map<String, Object>> exDataList = (List<Map<String, Object>>) tData.get("exercicios");
            if (exDataList != null) {
                for (Map<String, Object> exMap : exDataList) {
                    ExercicioDTO dto = new ExercicioDTO();
                    dto.setNome((String) exMap.get("nome"));
                    dto.setGrupoMuscular((String) exMap.get("grupoMuscular"));

                    // Tratamento seguro para inteiros
                    Object seriesObj = exMap.get("series");
                    dto.setSeries(seriesObj instanceof Integer ? (Integer) seriesObj
                            : Integer.parseInt(seriesObj.toString()));

                    Object repeticoesObj = exMap.get("repeticoes");
                    dto.setRepeticoes(repeticoesObj instanceof Integer ? (Integer) repeticoesObj
                            : Integer.parseInt(repeticoesObj.toString()));

                    // Tratamento seguro para double
                    Object pesoObj = exMap.get("pesoInicial");
                    dto.setPesoInicial(pesoObj instanceof Number ? ((Number) pesoObj).doubleValue()
                            : Double.parseDouble(pesoObj.toString()));

                    dto.setObservacao((String) exMap.get("observacao"));
                    dto.setAlunoId(aluno.getId());
                    dto.setAtivo(true);
                    dto.setGrupoId(grupoSalvo.getId());

                    exercicioService.criarExercicio(dto);
                }
            }
        }
    }

    /**
     * Faz a chamada HTTP para a API do Groq e retorna o texto da resposta.
     * Formato de resposta: choices[0].message.content
     */
    protected String chamarGroq(String payloadJson) {
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
    void salvarMensagem(UUID alunoId, String role, String conteudo) {
        IaMensagem msg = new IaMensagem();
        msg.setAlunoId(alunoId);
        msg.setRole(role);
        msg.setConteudo(conteudo);
        iaMensagemRepository.save(msg);
    }
}
