package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.TreinoDTO;
import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.service.ExercicioService;
import com.treino_abc_backend.service.TreinoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/treinos")
public class TreinoController {

    private final ExercicioService service;
    private final TreinoService treinoService;


    // Injeção via construtor
    public TreinoController(ExercicioService service, TreinoService treinoService) {
        this.service = service;
        this.treinoService = treinoService;
    }

    // Listar exercícios de um treino
    @GetMapping("/{treinoNome}")
    public ResponseEntity<List<Exercicio>> listarPorTreino(@RequestHeader("aluno-id") String alunoId,
                                                           @PathVariable String treinoNome) {
        // Aqui você pode filtrar por treinoNome se tiver esse campo, ou só listar do aluno
        return ResponseEntity.ok(service.getPorAluno(alunoId));
    }

    // Adicionar exercício a um treino
    @PostMapping("/{treinoNome}")
    public ResponseEntity<Exercicio> adicionar(@RequestHeader("aluno-id") String alunoId,
                                               @PathVariable String treinoNome,
                                               @RequestBody Exercicio exercicio) {
        exercicio.setAlunoId(UUID.fromString(alunoId));
        Exercicio salvo = service.adicionarAoTreino(exercicio);
        return ResponseEntity.ok(salvo);
    }

    // Remover exercício de um treino
    @DeleteMapping("/{treinoNome}/{id}")
    public ResponseEntity<Void> remover(@RequestHeader("aluno-id") String alunoId,
                                        @PathVariable String treinoNome,
                                        @PathVariable String id) {
        service.removerDoTreino(id, UUID.fromString(alunoId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<TreinoDTO>> listarPorGrupo(@RequestHeader("aluno-id") String alunoId,
                                                          @PathVariable UUID grupoId) {
        UUID alunoUUID = UUID.fromString(alunoId);
        List<TreinoDTO> treinos = treinoService.listarDTOsPorGrupo(grupoId).stream()
                .filter(t -> t.getAlunoId().equals(alunoUUID))
                .toList();

        return ResponseEntity.ok(treinos);
    }

}
