package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.IaMensagemRequestDTO;
import com.treino_abc_backend.dto.IaRespostaDTO;
import com.treino_abc_backend.security.JwtUtil;
import com.treino_abc_backend.service.IaCoachService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ia")
public class IaCoachController {

    private final IaCoachService iaCoachService;
    private final JwtUtil jwtUtil;

    public IaCoachController(IaCoachService iaCoachService, JwtUtil jwtUtil) {
        this.iaCoachService = iaCoachService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Chamado pelo Flutter ao abrir a aba IA Coach.
     * Verifica se o aluno possui treinos cadastrados:
     * - Se NÃO possui → retorna pergunta motivadora com opções de ação
     * (semTreinos=true, opcoes=["Criar treino e exercícios", "Perguntar algo sobre
     * o meu treino"])
     * - Se JÁ possui → retorna a análise normal do perfil via Groq
     */
    @GetMapping("/iniciar/{alunoId}")
    public ResponseEntity<?> iniciarConversa(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("alunoId") UUID alunoId) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("erro", "Token de autenticação ausente ou inválido"));
            }

            // Verifica se já tem treinos; se não tiver, retorna pergunta com opções
            IaRespostaDTO respostaSemTreinos = iaCoachService.verificarInicioAluno(alunoId);
            if (respostaSemTreinos != null) {
                return ResponseEntity.ok(respostaSemTreinos);
            }

            // Aluno já tem treinos — gera análise normal do perfil
            String analise = iaCoachService.gerarAnalise(alunoId);
            return ResponseEntity.ok(new IaRespostaDTO(analise));

        } catch (RuntimeException e) {
            System.err.println("[IaCoachController] Erro iniciar: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> chat(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody IaMensagemRequestDTO request) {
        try {

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("erro", "Token de autenticação ausente ou inválido"));
            }

            if (request.getAlunoId() == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "alunoId é obrigatório"));
            }
            if (request.getMensagem() == null || request.getMensagem().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("erro", "mensagem não pode ser vazia"));
            }

            // Processar mensagem e obter resposta da IA
            String respostaIA = iaCoachService.processarMensagem(
                    request.getAlunoId(),
                    request.getMensagem());

            return ResponseEntity.ok(new IaRespostaDTO(respostaIA));

        } catch (RuntimeException e) {
            System.err.println("[IaCoachController] Erro: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/analise/{alunoId}")
    public ResponseEntity<?> gerarAnalise(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("alunoId") UUID alunoId) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("erro", "Token de autenticação ausente ou inválido"));
            }
            String analise = iaCoachService.gerarAnalise(alunoId);
            return ResponseEntity.ok(new IaRespostaDTO(analise));
        } catch (RuntimeException e) {
            System.err.println("[IaCoachController] Erro analise: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        return ResponseEntity.ok(Map.of("status", "IA Coach online"));
    }
}
