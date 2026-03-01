package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.AlunoDTO;
import com.treino_abc_backend.dto.AlunoUpdateDTO;
import com.treino_abc_backend.service.AlunoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable("id") UUID id) {
        try {
            AlunoDTO dto = alunoService.buscarPorId(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}/fisico")
    public ResponseEntity<?> atualizarFisico(@PathVariable("id") UUID id, @RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(alunoService.atualizarFisico(id, body));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}/objetivo")
    public ResponseEntity<?> atualizarObjetivo(@PathVariable("id") UUID id, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(alunoService.atualizarObjetivo(id, body.get("objetivo"), body.get("nivelTreinamento")));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarAluno(
            @PathVariable("id") UUID id,
            @RequestBody AlunoUpdateDTO alunoDTO
    ) {
        try {
            AlunoDTO dto = alunoService.atualizarCompleto(id, alunoDTO);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("erro", e.getMessage()));
        }
    }

}
