package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.FichaTreinoDTO;
import com.treino_abc_backend.dto.TreinoDTO;
import com.treino_abc_backend.dto.TreinoInputDTO;
import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.entity.TreinoExercicioAluno;
import com.treino_abc_backend.service.TreinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/treinos")
public class TreinoController {

    @Autowired
    private TreinoService treinoService;

    @PostMapping
    public ResponseEntity<TreinoExercicioAluno> adicionar(@RequestBody TreinoInputDTO dto) {
        TreinoExercicioAluno salvo = treinoService.adicionar(
                dto.getAlunoId(),
                dto.getGrupoId(),
                dto.getExercicioId(),
                dto.getDiaSemana(),
                dto.getOrdem(),
                dto.getObservacao()
        );
        return ResponseEntity.status(201).body(salvo);
    }

    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<TreinoDTO>> listarPorGrupo(@PathVariable UUID grupoId) {
        List<TreinoDTO> treinos = treinoService.listarDTOsPorGrupo(grupoId);
        return ResponseEntity.ok(treinos);
    }

    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<TreinoExercicioAluno>> listarPorAluno(@PathVariable UUID alunoId) {
        List<TreinoExercicioAluno> treinos = treinoService.listarPorAluno(alunoId);
        return ResponseEntity.ok(treinos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TreinoExercicioAluno> atualizar(@PathVariable UUID id, @RequestBody TreinoDTO dto) {
        TreinoExercicioAluno atualizado = treinoService.atualizarViaDTO(id, dto);
        return ResponseEntity.ok(atualizado);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        treinoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ficha/aluno/{alunoId}")
    public ResponseEntity<Map<String, List<FichaTreinoDTO>>> fichaPorAluno(@PathVariable UUID alunoId) {
        Map<String, List<FichaTreinoDTO>> ficha = treinoService.fichaPorAluno(alunoId);
        return ResponseEntity.ok(ficha);
    }


}
