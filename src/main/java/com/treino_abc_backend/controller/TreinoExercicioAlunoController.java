package com.treino_abc_backend.controller;

import com.treino_abc_backend.entity.TreinoExercicioAluno;
import com.treino_abc_backend.enums.StatusExecucaoExercicio;
import com.treino_abc_backend.repository.TreinoExercicioAlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/treino-exercicio-aluno")
public class TreinoExercicioAlunoController {

    @Autowired
    private TreinoExercicioAlunoRepository repository;

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
            System.out.println("[DEBUG] PATCH /treino-exercicio-aluno/" + id + "/status chamado. Body: " + body);
        try {
            TreinoExercicioAluno treinoExercicio = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado"));

            String novoStatus = body.get("status");
            if (novoStatus == null) {
                return ResponseEntity.badRequest().body("Campo 'status' obrigatório");
            }

            treinoExercicio.setStatus(StatusExecucaoExercicio.valueOf(novoStatus));
            repository.save(treinoExercicio);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Status inválido: " + e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno: " + ex.getMessage());
        }
    }
}
