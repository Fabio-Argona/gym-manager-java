package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.ExercicioRealizadoDTO;
import com.treino_abc_backend.dto.RegistrarExercicioDTO;
import com.treino_abc_backend.service.ExercicioRealizadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/exercicios-realizados")
public class ExercicioRealizadoController {

    private final ExercicioRealizadoService exercicioRealizadoService;

    public ExercicioRealizadoController(ExercicioRealizadoService exercicioRealizadoService) {
        this.exercicioRealizadoService = exercicioRealizadoService;
    }

    /**
     * Registra um exercício realizado pelo aluno
     * POST /exercicios-realizados
     */
    @PostMapping
    public ResponseEntity<ExercicioRealizadoDTO> registrarExercicio(@RequestBody RegistrarExercicioDTO dto) {
        try {
            ExercicioRealizadoDTO resultado = exercicioRealizadoService.registrarExercicio(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Busca histórico de progressão de exercícios do aluno
     * GET /exercicios-realizados/progressao?alunoId=UUID&exercicioId=UUID(opcional)&dataInicio=yyyy-MM-dd(opcional)&dataFim=yyyy-MM-dd(opcional)
     */
    @GetMapping("/progressao")
    public ResponseEntity<List<ExercicioRealizadoDTO>> buscarProgressao(
            @RequestParam("alunoId") UUID alunoId,
            @RequestParam(required = false) UUID exercicioId,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim
    ) {
        try {
            List<ExercicioRealizadoDTO> resultado;
            if (exercicioId != null && dataInicio != null && dataFim != null) {
                resultado = exercicioRealizadoService.buscarProgressaoExercicio(
                        alunoId, exercicioId,
                        LocalDate.parse(dataInicio),
                        LocalDate.parse(dataFim)
                );
            } else if (exercicioId != null) {
                resultado = exercicioRealizadoService.buscarHistoricoExercicio(alunoId, exercicioId);
            } else {
                resultado = exercicioRealizadoService.buscarTodaProgressao(alunoId);
            }
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

