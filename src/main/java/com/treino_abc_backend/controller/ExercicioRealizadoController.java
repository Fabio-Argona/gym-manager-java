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
     * Body: RegistrarExercicioDTO
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
     * Busca exercícios realizados por aluno em uma data específica
     * GET /exercicios-realizados?aluno-id=UUID&data=yyyy-MM-dd
     */
    @GetMapping
    public ResponseEntity<List<ExercicioRealizadoDTO>> buscarPorData(
            @RequestParam("aluno-id") UUID alunoId,
            @RequestParam("data") String dataStr
    ) {
        try {
            LocalDate data = LocalDate.parse(dataStr);
            List<ExercicioRealizadoDTO> resultado = exercicioRealizadoService.buscarPorData(alunoId, data);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Busca exercícios em um período (para gráficos)
     * GET /exercicios-realizados/periodo?aluno-id=UUID&data-inicio=yyyy-MM-dd&data-fim=yyyy-MM-dd
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<ExercicioRealizadoDTO>> buscarPorPeriodo(
            @RequestParam("aluno-id") UUID alunoId,
            @RequestParam("data-inicio") String dataInicioStr,
            @RequestParam("data-fim") String dataFimStr
    ) {
        try {
            LocalDate dataInicio = LocalDate.parse(dataInicioStr);
            LocalDate dataFim = LocalDate.parse(dataFimStr);
            List<ExercicioRealizadoDTO> resultado = exercicioRealizadoService.buscarPorPeriodo(alunoId, dataInicio, dataFim);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Busca o histórico de progressão de um exercício específico
     * GET /exercicios-realizados/progressao?aluno-id=UUID&exercicio-id=UUID&data-inicio=yyyy-MM-dd&data-fim=yyyy-MM-dd
     */
    @GetMapping("/progressao")
    public ResponseEntity<List<ExercicioRealizadoDTO>> buscarProgressao(
            @RequestParam("aluno-id") UUID alunoId,
            @RequestParam("exercicio-id") UUID exercicioId,
            @RequestParam("data-inicio") String dataInicioStr,
            @RequestParam("data-fim") String dataFimStr
    ) {
        try {
            LocalDate dataInicio = LocalDate.parse(dataInicioStr);
            LocalDate dataFim = LocalDate.parse(dataFimStr);
            List<ExercicioRealizadoDTO> resultado = exercicioRealizadoService.buscarProgressaoExercicio(
                    alunoId, exercicioId, dataInicio, dataFim
            );
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Busca histórico completo de um exercício
     * GET /exercicios-realizados/historico?aluno-id=UUID&exercicio-id=UUID
     */
    @GetMapping("/historico")
    public ResponseEntity<List<ExercicioRealizadoDTO>> buscarHistoricoExercicio(
            @RequestParam("aluno-id") UUID alunoId,
            @RequestParam("exercicio-id") UUID exercicioId
    ) {
        try {
            List<ExercicioRealizadoDTO> resultado = exercicioRealizadoService.buscarHistoricoExercicio(alunoId, exercicioId);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Busca exercícios de uma sessão de treino
     * GET /exercicios-realizados/sessao/{treino-realizado-id}
     */
    @GetMapping("/sessao/{treinoRealizadoId}")
    public ResponseEntity<List<ExercicioRealizadoDTO>> buscarPorSessao(
            @PathVariable UUID treinoRealizadoId
    ) {
        try {
            List<ExercicioRealizadoDTO> resultado = exercicioRealizadoService.buscarPorSessao(treinoRealizadoId);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Atualiza um exercício realizado
     * PUT /exercicios-realizados/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExercicioRealizadoDTO> atualizarExercicio(
            @PathVariable UUID id,
            @RequestBody RegistrarExercicioDTO dto
    ) {
        try {
            ExercicioRealizadoDTO resultado = exercicioRealizadoService.atualizar(id, dto);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Deleta um exercício realizado
     * DELETE /exercicios-realizados/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarExercicio(@PathVariable UUID id) {
        try {
            exercicioRealizadoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
