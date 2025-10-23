package com.treino_abc_backend.controller;

import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.service.ExercicioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/exercicios")
public class ExercicioController {

    private final ExercicioService service;

    public ExercicioController(ExercicioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Exercicio>> listar(@RequestHeader("aluno-id") String alunoId) {
        // Lista apenas os exercícios do aluno logado
        return ResponseEntity.ok(service.getPorAluno(alunoId));
    }

    @PostMapping
    public ResponseEntity<Exercicio> criar(@RequestHeader("aluno-id") String alunoId,
                                           @RequestBody Exercicio exercicio) {
        exercicio.setAlunoId(UUID.fromString(alunoId));
        Exercicio salvo = service.salvar(exercicio);
        return ResponseEntity.status(201).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Exercicio> atualizar(@RequestHeader("aluno-id") String alunoId,
                                               @PathVariable String id,
                                               @RequestBody Exercicio exercicio) {
        exercicio.setId(UUID.fromString(id));
        return service.atualizar(exercicio, UUID.fromString(alunoId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(403).build()); // 403 se tentar atualizar outro aluno
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@RequestHeader("aluno-id") String alunoId,
                                        @PathVariable String id) {
        service.deletar(id, UUID.fromString(alunoId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/treino/{treinoNome}")
    public ResponseEntity<Exercicio> adicionarAoTreino(@RequestHeader("aluno-id") String alunoId,
                                                       @PathVariable String treinoNome,
                                                       @RequestBody Exercicio exercicio) {
        exercicio.setAlunoId(UUID.fromString(alunoId));
        Exercicio salvo = service.adicionarAoTreino(exercicio);
        return ResponseEntity.ok(salvo);
    }

    @DeleteMapping("/treino/{treinoNome}/{id}")
    public ResponseEntity<Void> removerDoTreino(@RequestHeader("aluno-id") String alunoId,
                                                @PathVariable String treinoNome,
                                                @PathVariable String id) {
        service.removerDoTreino(id, UUID.fromString(alunoId));
        return ResponseEntity.ok().build();
    }
}
