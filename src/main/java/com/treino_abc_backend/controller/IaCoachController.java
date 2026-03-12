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
            IaRespostaDTO analise = iaCoachService.gerarAnalise(alunoId);
            return ResponseEntity.ok(analise);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[IaCoachController] Erro iniciar: " + e.getMessage());
            String erroMsg = e.getMessage() != null ? e.getMessage() : e.toString();
            return ResponseEntity.status(500).body(Map.of("erro", erroMsg));
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
            IaRespostaDTO respostaDTO = iaCoachService.processarMensagem(
                    request.getAlunoId(),
                    request.getMensagem());

            return ResponseEntity.ok(respostaDTO);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[IaCoachController] Erro chat: " + e.getMessage());
            String erroMsg = e.getMessage() != null ? e.getMessage() : e.toString();
            return ResponseEntity.status(500).body(Map.of("erro", erroMsg));
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
            IaRespostaDTO analise = iaCoachService.gerarAnalise(alunoId);
            return ResponseEntity.ok(analise);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[IaCoachController] Erro analise: " + e.getMessage());
            String erroMsg = e.getMessage() != null ? e.getMessage() : e.toString();
            return ResponseEntity.status(500).body(Map.of("erro", erroMsg));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        return ResponseEntity.ok(Map.of("status", "IA Coach online"));
    }
}
