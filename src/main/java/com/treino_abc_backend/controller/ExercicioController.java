package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.ExercicioDTO;
import com.treino_abc_backend.dto.ExercicioEdicaoDTO;
import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.entity.TreinoGrupo;
import com.treino_abc_backend.repository.ExercicioRepository;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import com.treino_abc_backend.service.ExercicioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/exercicios")
public class ExercicioController {

    private final ExercicioService service;
    private final ExercicioRepository exercicioRepository;
    private final TreinoGrupoRepository treinoGrupoRepository;

    public ExercicioController(ExercicioService service, ExercicioRepository exercicioRepository, TreinoGrupoRepository treinoGrupoRepository) {
        this.service = service;
        this.exercicioRepository = exercicioRepository;
        this.treinoGrupoRepository = treinoGrupoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Exercicio>> listar(@RequestHeader("aluno-id") String alunoId) {
        return ResponseEntity.ok(service.getPorAluno(alunoId));
    }

    @PostMapping
    public ResponseEntity<ExercicioDTO> criar(@RequestHeader("aluno-id") String alunoId,
                                              @RequestBody ExercicioDTO dto) {
        dto.setAlunoId(UUID.fromString(alunoId));
        Exercicio salvo = service.criarExercicio(dto);
        return ResponseEntity.status(201).body(toDTO(salvo));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Exercicio> atualizar(@PathVariable UUID id,
                                               @RequestBody ExercicioEdicaoDTO dto,
                                               @RequestHeader("aluno-id") String alunoId) {
        Exercicio existente = exercicioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!existente.getAlunoId().equals(UUID.fromString(alunoId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        existente.setNome(dto.getNome());
        existente.setGrupoMuscular(dto.getGrupoMuscular());
        existente.setSeries(dto.getSeries());
        existente.setRepMin(dto.getRepMin());
        existente.setRepMax(dto.getRepMax());
        existente.setPesoInicial(dto.getPesoInicial());
        existente.setObservacao(dto.getObservacao());
        existente.setAtivo(dto.isAtivo());

        if (dto.getGrupoId() != null) {
            TreinoGrupo grupo = treinoGrupoRepository.findById(dto.getGrupoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo não encontrado"));
            existente.setGrupo(grupo);
        }

        return ResponseEntity.ok(exercicioRepository.save(existente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@RequestHeader("aluno-id") String alunoId,
                                        @PathVariable String id) {
        service.deletar(id, UUID.fromString(alunoId));
        return ResponseEntity.noContent().build();
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
        return ResponseEntity.noContent().build();
    }

    private ExercicioDTO toDTO(Exercicio e) {
        ExercicioDTO dto = new ExercicioDTO();
        dto.setId(e.getId());
        dto.setNome(e.getNome());
        dto.setGrupoMuscular(e.getGrupoMuscular());
        dto.setSeries(e.getSeries());
        dto.setRepMin(e.getRepMin());
        dto.setRepMax(e.getRepMax());
        dto.setPesoInicial(e.getPesoInicial());
        dto.setObservacao(e.getObservacao());
        dto.setAlunoId(e.getAlunoId());
        dto.setAtivo(e.isAtivo());
        dto.setDataCriacao(e.getDataCriacao());
        dto.setGrupoId(e.getGrupoId());
        return dto;
    }
}
