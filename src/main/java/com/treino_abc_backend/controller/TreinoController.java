package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.TreinoDTO;
import com.treino_abc_backend.dto.TreinoGrupoDTO;
import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.security.JwtUtil;
import com.treino_abc_backend.service.ExercicioService;
import com.treino_abc_backend.service.TreinoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/treinos")
public class TreinoController {

    private final ExercicioService exercicioService;
    private final TreinoService treinoService;
    private final JwtUtil jwtUtil;
    private final AlunoRepository alunoRepo;

    public TreinoController(
            ExercicioService exercicioService,
            TreinoService treinoService,
            JwtUtil jwtUtil,
            AlunoRepository alunoRepo
    ) {
        this.exercicioService = exercicioService;
        this.treinoService = treinoService;
        this.jwtUtil = jwtUtil;
        this.alunoRepo = alunoRepo;
    }

    //  Listar exercícios de um treino (por nome, se necessário)
    @GetMapping("/{treinoNome}")
    public ResponseEntity<List<Exercicio>> listarPorTreino(
            @RequestHeader("aluno-id") String alunoId,
            @PathVariable String treinoNome
    ) {
        return ResponseEntity.ok(exercicioService.getPorAluno(alunoId));
    }

    //  Adicionar exercício a um treino
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

    //  Remover exercício de um treino
    @DeleteMapping("/{treinoNome}/{id}")
    public ResponseEntity<Void> remover(
            @RequestHeader("aluno-id") String alunoId,
            @PathVariable String treinoNome,
            @PathVariable String id
    ) {
        exercicioService.removerDoTreino(id, UUID.fromString(alunoId));
        return ResponseEntity.ok().build();
    }

    //  Listar grupos com exercícios do aluno logado (via token JWT)
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
}
