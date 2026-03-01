package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.ExercicioRealizadoDTO;
import com.treino_abc_backend.dto.RegistrarExercicioDTO;
import com.treino_abc_backend.entity.ExercicioRealizado;
import com.treino_abc_backend.entity.TreinoRealizado;
import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.repository.ExercicioRealizadoRepository;
import com.treino_abc_backend.repository.TreinoRealizadoRepository;
import com.treino_abc_backend.repository.ExercicioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ExercicioRealizadoService {

    private final ExercicioRealizadoRepository exercicioRealizadoRepo;
    private final TreinoRealizadoRepository treinoRealizadoRepo;
    private final ExercicioRepository exercicioRepo;

    public ExercicioRealizadoService(ExercicioRealizadoRepository exercicioRealizadoRepo,
                                   TreinoRealizadoRepository treinoRealizadoRepo,
                                   ExercicioRepository exercicioRepo) {
        this.exercicioRealizadoRepo = exercicioRealizadoRepo;
        this.treinoRealizadoRepo = treinoRealizadoRepo;
        this.exercicioRepo = exercicioRepo;
    }

    /**
     * Registra um exercício realizado pelo aluno
     * Salva as informações de séries, repetições, peso e observações
     */
    public ExercicioRealizadoDTO registrarExercicio(RegistrarExercicioDTO dto) {
        // Buscar TreinoRealizado (sessão de treino)
        TreinoRealizado treinoRealizado = treinoRealizadoRepo.findById(dto.getTreino_realizado_id())
                .orElseThrow(() -> new IllegalArgumentException("Treino realizado não encontrado"));

        // Buscar Exercício
        Exercicio exercicio = exercicioRepo.findById(dto.getExercicio_id())
                .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado"));

        // Criar nova entidade
        ExercicioRealizado exercicioRealizado = new ExercicioRealizado();
        exercicioRealizado.setTreinoRealizado(treinoRealizado);
        exercicioRealizado.setExercicio(exercicio);
        exercicioRealizado.setSeriesRealizadas(dto.getSeries_realizadas());
        exercicioRealizado.setRepeticoesRealizadas(dto.getRepeticoes_realizadas());
        exercicioRealizado.setPesoUtilizado(dto.getPeso_utilizado());
        exercicioRealizado.setObservacoes(dto.getObservacoes());
        exercicioRealizado.setDataSessao(dto.getData_sessao() != null ? dto.getData_sessao() : LocalDate.now());
        exercicioRealizado.setCriadoEm(LocalDateTime.now());

        ExercicioRealizado salvo = exercicioRealizadoRepo.save(exercicioRealizado);

        // Retornar DTO
        return converterParaDTO(salvo, exercicio.getNome());
    }

    public List<ExercicioRealizadoDTO> buscarProgressaoExercicio(UUID alunoId, UUID exercicioId,
                                                                 LocalDate dataInicio, LocalDate dataFim) {
        List<ExercicioRealizado> exercicios = exercicioRealizadoRepo.findProgressao(alunoId, exercicioId, dataInicio, dataFim);
        return exercicios.stream()
                .map(er -> converterParaDTO(er, er.getExercicio().getNome()))
                .toList();
    }

    public List<ExercicioRealizadoDTO> buscarHistoricoExercicio(UUID alunoId, UUID exercicioId) {
        List<ExercicioRealizado> exercicios = exercicioRealizadoRepo.findByAlunoIdAndExercicioId(alunoId, exercicioId);
        return exercicios.stream()
                .map(er -> converterParaDTO(er, er.getExercicio().getNome()))
                .toList();
    }

    public List<ExercicioRealizadoDTO> buscarTodaProgressao(UUID alunoId) {
        return exercicioRealizadoRepo.findByAlunoId(alunoId).stream()
                .map(er -> converterParaDTO(er, er.getExercicio().getNome()))
                .toList();
    }

    private ExercicioRealizadoDTO converterParaDTO(ExercicioRealizado exercicio, String nomeExercicio) {
        return new ExercicioRealizadoDTO(
                exercicio.getId(),
                exercicio.getExercicio().getId(),
                nomeExercicio,
                exercicio.getSeriesRealizadas(),
                exercicio.getRepeticoesRealizadas(),
                exercicio.getPesoUtilizado(),
                exercicio.getObservacoes(),
                exercicio.getDataSessao(),
                exercicio.getCriadoEm()
        );
    }
}
