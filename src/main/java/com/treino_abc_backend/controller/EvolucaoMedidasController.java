package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.EvolucaoMedidasDTO;
import com.treino_abc_backend.entity.EvolucaoMedidas;
import com.treino_abc_backend.service.EvolucaoMedidasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/evolucoes")
public class EvolucaoMedidasController {

    private final EvolucaoMedidasService evolucaoService;

    public EvolucaoMedidasController(EvolucaoMedidasService evolucaoService) {
        this.evolucaoService = evolucaoService;
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody EvolucaoMedidasDTO dto) {
        try {
            EvolucaoMedidas salvo = evolucaoService.salvar(dto);
            return ResponseEntity.ok(salvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable("id") UUID id, @RequestBody EvolucaoMedidasDTO dto) {
        try {
            EvolucaoMedidas atualizado = evolucaoService.atualizar(id, dto);
            return ResponseEntity.ok(atualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<?> listarPorAluno(@PathVariable("alunoId") UUID alunoId) {
        try {
            List<EvolucaoMedidas> lista = evolucaoService.listarPorAluno(alunoId);
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("erro", e.getMessage()));
        }
    }
}
