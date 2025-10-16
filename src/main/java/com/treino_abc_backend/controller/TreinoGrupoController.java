package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.TreinoGrupoDTO;
import com.treino_abc_backend.service.TreinoGrupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/grupos")
public class TreinoGrupoController {

    @Autowired
    private TreinoGrupoService grupoService;

    @PostMapping
    public ResponseEntity<TreinoGrupoDTO> criar(@RequestBody TreinoGrupoDTO dto) {
        TreinoGrupoDTO salvo = grupoService.criar(dto);
        return ResponseEntity.status(201).body(salvo);
    }


    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<TreinoGrupoDTO>> listarPorAluno(@PathVariable UUID alunoId) {
        List<TreinoGrupoDTO> grupos = grupoService.listarPorAluno(alunoId);
        return ResponseEntity.ok(grupos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        grupoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TreinoGrupoDTO> editarGrupo(
            @PathVariable UUID id,
            @RequestBody TreinoGrupoDTO dto
    ) {
        try {
            TreinoGrupoDTO atualizado = grupoService.editar(id, dto);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
