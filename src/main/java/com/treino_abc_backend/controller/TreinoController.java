package com.treino_abc_backend.controller;

import com.treino_abc_backend.entity.TreinoRealizado;
import com.treino_abc_backend.service.TreinoRealizadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/treinos")
public class TreinoController {

    private final TreinoRealizadoService realizadoService;

    public TreinoController(TreinoRealizadoService realizadoService) {
        this.realizadoService = realizadoService;
    }

    /**
     * Registra uma sessão de treino a partir do ID do grupo
     * POST /treinos/realizado/{grupoId}?data=yyyy-MM-dd
     */
    @PostMapping("/realizado/{grupoId}")
    public ResponseEntity<?> registrarTreino(
            @PathVariable("grupoId") UUID grupoId,
            @RequestParam(required = false) String data
    ) {
        try {
            LocalDate dia = data != null ? LocalDate.parse(data) : LocalDate.now();
            TreinoRealizado realizado = realizadoService.registrarPorGrupo(grupoId, dia);
            return ResponseEntity.ok(Map.of(
                    "id", realizado.getId().toString(),
                    "grupoId", realizado.getGrupo().getId().toString(),
                    "alunoId", realizado.getAlunoId().toString(),
                    "data", realizado.getData().toString()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Bad Request",
                    "message", e.getMessage(),
                    "status", 400
            ));
        }
    }

    /**
     * Lista todas as sessões de treino de um aluno
     * GET /treinos/realizado/aluno/{alunoId}
     */
    @GetMapping("/realizado/aluno/{alunoId}")
    public ResponseEntity<List<Map<String, String>>> listarSessoesPorAluno(
            @PathVariable("alunoId") UUID alunoId
    ) {
        List<TreinoRealizado> sessoes = realizadoService.buscarSessoesPorAluno(alunoId);
        List<Map<String, String>> resultado = sessoes.stream()
                .map(tr -> Map.of(
                        "id", tr.getId().toString(),
                        "data", tr.getData().toString(),
                        "grupoNome", tr.getGrupo().getNome(),
                        "alunoId", tr.getAlunoId().toString()
                ))
                .toList();
        return ResponseEntity.ok(resultado);
    }

    /**
     * Busca a última sessão de um grupo específico
     * GET /treinos/realizado/grupo/{grupoId}/ultima
     */
    @GetMapping("/realizado/grupo/{grupoId}/ultima")
    public ResponseEntity<?> buscarUltimaSessao(@PathVariable("grupoId") UUID grupoId) {
        return realizadoService.buscarUltimaPorGrupo(grupoId)
                .<ResponseEntity<?>>map(tr -> ResponseEntity.ok(Map.of(
                        "id", tr.getId().toString(),
                        "grupoId", tr.getGrupo().getId().toString(),
                        "alunoId", tr.getAlunoId().toString(),
                        "data", tr.getData().toString()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
}
