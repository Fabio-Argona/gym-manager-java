package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.TreinoGrupoDTO;
import com.treino_abc_backend.service.TreinoGrupoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/grupos")
public class TreinoGrupoController {

    private final TreinoGrupoService grupoService;

    public TreinoGrupoController(TreinoGrupoService grupoService) {
        this.grupoService = grupoService;
    }

    @PostMapping
    public ResponseEntity<TreinoGrupoDTO> criar(@RequestBody TreinoGrupoDTO dto) {
        TreinoGrupoDTO salvo = grupoService.criar(dto);
        return ResponseEntity.status(201).body(salvo);
    }

    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<TreinoGrupoDTO>> listarPorAluno(@PathVariable("alunoId") UUID alunoId) {
        List<TreinoGrupoDTO> grupos = grupoService.listarPorAluno(alunoId);
        return ResponseEntity.ok(grupos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable("id") UUID id) {
        grupoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TreinoGrupoDTO> editarGrupo(@PathVariable("id") UUID id, @RequestBody TreinoGrupoDTO dto) {
        try {
            TreinoGrupoDTO atualizado = grupoService.editar(id, dto);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}/com-exercicios")
    public ResponseEntity<?> excluirGrupoComExercicios(@PathVariable("id") UUID id) {
        try {
            grupoService.excluirGrupoComExercicios(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao excluir grupo e exercícios: " + e.getMessage());
        }
    }

}
