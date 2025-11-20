package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.ExercicioDTO;
import com.treino_abc_backend.dto.ExercicioEdicaoDTO;
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
    public ResponseEntity<List<ExercicioDTO>> listar(@RequestHeader("aluno-id") String alunoId) {
        return ResponseEntity.ok(service.getPorAluno(UUID.fromString(alunoId)));
    }

    @PostMapping
    public ResponseEntity<ExercicioDTO> criar(@RequestHeader("aluno-id") String alunoId,
                                              @RequestBody ExercicioDTO dto) {
        dto.setAlunoId(UUID.fromString(alunoId));
        Exercicio salvo = service.criarExercicio(dto);
        return ResponseEntity.status(201).body(service.toDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExercicioDTO> atualizar(@PathVariable UUID id,
                                                  @RequestBody ExercicioEdicaoDTO dto,
                                                  @RequestHeader("aluno-id") String alunoId) {
        Exercicio atualizado = service.atualizar(id, dto, UUID.fromString(alunoId));
        return ResponseEntity.ok(service.toDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@RequestHeader("aluno-id") String alunoId,
                                        @PathVariable UUID id) {
        service.deletar(id, UUID.fromString(alunoId));
        return ResponseEntity.noContent().build();
    }
}
