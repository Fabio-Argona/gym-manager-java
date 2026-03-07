package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.IaMensagemRequestDTO;
import com.treino_abc_backend.dto.IaRespostaDTO;
import com.treino_abc_backend.security.JwtUtil;
import com.treino_abc_backend.service.IaCoachService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller do chat IA Coach.
 * Endpoint: POST /ia
 * Requer: Authorization: Bearer <token>
 */
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
     * Recebe uma mensagem do aluno e retorna a resposta da IA Coach.
     *
     * Body JSON: { "alunoId": "uuid", "mensagem": "texto" }
     * Header: Authorization: Bearer <token>
     */
    @PostMapping
    public ResponseEntity<?> chat(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody IaMensagemRequestDTO request) {
        try {
            // Validar presença do token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("erro", "Token de autenticação ausente ou inválido"));
            }

            // Validar campos obrigatórios
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

    /**
     * Endpoint de health check da IA (opcional, útil para debug).
     * GET /ia/status — público, não requer token.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        return ResponseEntity.ok(Map.of("status", "IA Coach online"));
    }
}
