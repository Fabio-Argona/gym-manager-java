package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.ExercicioDTO;
import com.treino_abc_backend.dto.TreinoGrupoDTO;
import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.enums.StatusExecucaoExercicio;
import com.treino_abc_backend.enums.StatusTreino;
import com.treino_abc_backend.entity.Treino;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.security.JwtUtil;
import com.treino_abc_backend.service.ExercicioService;
import com.treino_abc_backend.service.TreinoRealizadoService;
import com.treino_abc_backend.service.TreinoService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/treinos")
public class TreinoController {

    private final ExercicioService exercicioService;
    private final TreinoService treinoService;
    private final JwtUtil jwtUtil;
    private final AlunoRepository alunoRepo;
    private final TreinoRealizadoService realizadoService;

    public TreinoController(
            ExercicioService exercicioService,
            TreinoService treinoService,
            JwtUtil jwtUtil,
            AlunoRepository alunoRepo,
            TreinoRealizadoService realizadoService
    ) {
        this.exercicioService = exercicioService;
        this.treinoService = treinoService;
        this.jwtUtil = jwtUtil;
        this.alunoRepo = alunoRepo;
        this.realizadoService = realizadoService;
    }

    // ---------------------------------------------------------------
    // LISTAR EXERCÍCIOS DE UM TREINO
    // ---------------------------------------------------------------
    @GetMapping("/{treinoNome}")
    public ResponseEntity<List<ExercicioDTO>> listarPorTreino(
            @RequestHeader("aluno-id") String alunoId,
            @PathVariable String treinoNome
    ) {
        UUID alunoUUID = UUID.fromString(alunoId);
        return ResponseEntity.ok(exercicioService.getPorAluno(alunoUUID));
    }

    // ---------------------------------------------------------------
    // ADICIONAR EXERCÍCIO AO TREINO
    // ---------------------------------------------------------------
    @PostMapping("/{treinoNome}")
    public ResponseEntity<Exercicio> adicionar(
            @RequestHeader("aluno-id") String alunoId,
            @PathVariable String treinoNome,
            @RequestBody Exercicio exercicio
    ) {
        exercicio.setAlunoId(UUID.fromString(alunoId));
        Exercicio salvo = exercicioService.adicionarAoTreino(exercicio);
        return ResponseEntity.ok(salvo);
    }

    // ---------------------------------------------------------------
    // REMOVER EXERCÍCIO DO TREINO
    // ---------------------------------------------------------------
    @DeleteMapping("/{treinoNome}/{id}")
    public ResponseEntity<Void> remover(
            @RequestHeader("aluno-id") String alunoId,
            @PathVariable String treinoNome,
            @PathVariable String id
    ) {
        exercicioService.removerDoTreino(id, UUID.fromString(alunoId));
        return ResponseEntity.ok().build();
    }

    // ---------------------------------------------------------------
    // EXCLUIR GRUPO COM EXERCÍCIOS
    // ---------------------------------------------------------------
    @DeleteMapping("/{id}/com-exercicios")
    public ResponseEntity<?> excluirGrupoComExercicios(@PathVariable UUID id) {
        try {
            treinoService.excluirGrupoComExercicios(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao excluir grupo e exercícios: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // LISTAR TREINOS DO ALUNO
    // ---------------------------------------------------------------
    @GetMapping
    public ResponseEntity<?> listarTreinosDoAluno(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractUsername(token);
            UUID alunoId = alunoRepo.findByEmail(email).orElseThrow().getId();

            List<TreinoGrupoDTO> grupos = treinoService.listarGruposComExercicios(alunoId);
            return ResponseEntity.ok(grupos);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido ou aluno não encontrado");
        }
    }

    // ---------------------------------------------------------------
    // ATUALIZAR STATUS DE UM EXERCÍCIO
    // ---------------------------------------------------------------
    @PatchMapping("/exercicios/{id}/status")
    public ResponseEntity<Exercicio> atualizarStatusExercicio(
            @PathVariable UUID id,
            @RequestBody Map<String, String> payload
    ) {
        return getExercicioResponseEntity(id, payload, exercicioService);
    }

    @NotNull
    static ResponseEntity<Exercicio> getExercicioResponseEntity(@PathVariable UUID id, @RequestBody Map<String, String> payload, ExercicioService exercicioService) {
        String statusStr = payload.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().build();
        }

        StatusExecucaoExercicio novoStatus;
        try {
            novoStatus = StatusExecucaoExercicio.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        Exercicio atualizado = exercicioService.atualizarStatusExecucao(id, novoStatus);
        return ResponseEntity.ok(atualizado);
    }

    // ---------------------------------------------------------------
    // ATUALIZAR STATUS DO TREINO MANUALMENTE
    // ---------------------------------------------------------------
    @PatchMapping("/{id}/status")
    public ResponseEntity<Treino> atualizarStatusTreino(
            @PathVariable UUID id,
            @RequestHeader("aluno-id") UUID alunoId,
            @RequestBody Map<String, String> payload
    ) {
        String statusStr = payload.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().body(null);
        }

        StatusTreino novoStatus;
        try {
            novoStatus = StatusTreino.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }

        Treino atualizado = treinoService.atualizarStatus(id, novoStatus, alunoId);
        return ResponseEntity.ok(atualizado);
    }

    // ---------------------------------------------------------------
    // RECALCULAR STATUS DO TREINO AUTOMATICAMENTE
    // ---------------------------------------------------------------
    @PatchMapping("/{id}/status/auto")
    public ResponseEntity<Treino> atualizarStatusAutomatico(@PathVariable UUID id) {
        Treino atualizado = treinoService.atualizarStatusAutomatico(id);
        return ResponseEntity.ok(atualizado);
    }

    // ---------------------------------------------------------------
    // REGISTRAR TREINO REALIZADO (aceita grupoId enviado pelo app)
    // ---------------------------------------------------------------
    @PostMapping("/realizado/{grupoId}")
    public ResponseEntity<?> registrarTreino(
            @PathVariable UUID grupoId,
            @RequestParam(required = false) String data // yyyy-MM-dd
    ) {
        try {
            LocalDate dia = data != null ? LocalDate.parse(data) : LocalDate.now();
            return ResponseEntity.ok(realizadoService.registrarPorGrupo(grupoId, dia));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of(
                            "error", "Bad Request",
                            "message", e.getMessage(),
                            "status", 400
                    )
            );
        }
    }

    // ---------------------------------------------------------------
    // LISTAR DATAS DE TREINOS REALIZADOS
    // ---------------------------------------------------------------
    @GetMapping("/realizados/{alunoId}")
    public ResponseEntity<List<LocalDate>> listarDatasTreinadas(@PathVariable UUID alunoId) {
        return ResponseEntity.ok(realizadoService.getDatasTreinadas(alunoId));
    }
}
