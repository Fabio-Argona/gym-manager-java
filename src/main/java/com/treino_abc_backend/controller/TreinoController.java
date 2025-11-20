package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.ExercicioDTO;
import com.treino_abc_backend.dto.TreinoGrupoDTO;
import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.security.JwtUtil;
import com.treino_abc_backend.service.ExercicioService;
import com.treino_abc_backend.service.TreinoRealizadoService;
import com.treino_abc_backend.service.TreinoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/treinos")
public class TreinoController {

    private final ExercicioService exercicioService;
    private final TreinoService treinoService;
    private final JwtUtil jwtUtil;
    private final AlunoRepository alunoRepo;
    private final TreinoRealizadoService realizadoService;

    public TreinoController(ExercicioService exercicioService, TreinoService treinoService, JwtUtil jwtUtil, AlunoRepository alunoRepo, TreinoRealizadoService realizadoService) {
        this.exercicioService = exercicioService;
        this.treinoService = treinoService;
        this.jwtUtil = jwtUtil;
        this.alunoRepo = alunoRepo;
        this.realizadoService = realizadoService;
    }


    @GetMapping("/{treinoNome}")
    public ResponseEntity<List<ExercicioDTO>> listarPorTreino(
            @RequestHeader("aluno-id") String alunoId,
            @PathVariable String treinoNome
    ) {
        UUID alunoUUID = UUID.fromString(alunoId);
        return ResponseEntity.ok(exercicioService.getPorAluno(alunoUUID));
    }


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

    @DeleteMapping("/{treinoNome}/{id}")
    public ResponseEntity<Void> remover(
            @RequestHeader("aluno-id") String alunoId,
            @PathVariable String treinoNome,
            @PathVariable String id
    ) {
        exercicioService.removerDoTreino(id, UUID.fromString(alunoId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/com-exercicios")
    public ResponseEntity<?> excluirGrupoComExercicios(@PathVariable UUID id) {
        try {
            treinoService.excluirGrupoComExercicios(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao excluir grupo e exercícios: " + e.getMessage());
        }
    }

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

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable UUID id, @RequestBody Map<String, Boolean> payload) {
        boolean novoStatus = payload.getOrDefault("ativo", true);

        Optional<Exercicio> exercicioOpt = exercicioService.atualizarStatus(id, novoStatus);
        if (exercicioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(exercicioOpt.get());
    }

    @PostMapping("/realizado/{treinoId}")
        public ResponseEntity<?> registrarTreino(
                @PathVariable UUID treinoId,
                @RequestParam(required = false) String data // yyyy-MM-dd
        ) {
            LocalDate dia = data != null ? LocalDate.parse(data) : LocalDate.now();
            return ResponseEntity.ok(realizadoService.registrar(treinoId, dia));
        }

        // Listar datas de treinos de um aluno
        @GetMapping("/realizados/{alunoId}")
        public ResponseEntity<List<LocalDate>> listarDatasTreinadas(@PathVariable UUID alunoId) {
            return ResponseEntity.ok(realizadoService.getDatasTreinadas(alunoId));
        }
    }

