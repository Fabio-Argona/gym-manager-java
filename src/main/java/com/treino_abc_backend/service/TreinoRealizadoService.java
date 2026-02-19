package com.treino_abc_backend.service;

import com.treino_abc_backend.entity.TreinoExercicioAluno;
import com.treino_abc_backend.entity.TreinoRealizado;
import com.treino_abc_backend.repository.TreinoExercicioAlunoRepository;
import com.treino_abc_backend.repository.TreinoRealizadoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TreinoRealizadoService {

    private final TreinoRealizadoRepository realizadoRepo;
    private final TreinoExercicioAlunoRepository treinoRepo;

    public TreinoRealizadoService(TreinoRealizadoRepository realizadoRepo,
                                  TreinoExercicioAlunoRepository treinoRepo) {
        this.realizadoRepo = realizadoRepo;
        this.treinoRepo = treinoRepo;
    }

    /**
     * Registrar que o treino foi feito em determinada data
     * Ativa um treino criando uma sessão de treino (TreinoRealizado) para aquele dia
     */
    public TreinoRealizado registrar(UUID treinoId, LocalDate data) {
        TreinoExercicioAluno treino = treinoRepo.findById(treinoId)
                .orElseThrow(() -> new IllegalArgumentException("Treino não encontrado"));

        // Verificar se já existe uma sessão para este treino nesta data
        Optional<TreinoRealizado> existente = realizadoRepo.findByTreinoAlunoId(treino.getAlunoId())
                .stream()
                .filter(tr -> tr.getTreino().getId().equals(treinoId) && tr.getData().equals(data))
                .findFirst();

        if (existente.isPresent()) {
            return existente.get();
        }

        TreinoRealizado realizado = new TreinoRealizado();
        realizado.setTreino(treino);
        realizado.setData(data);

        return realizadoRepo.save(realizado);
    }

    /**
     * Obter todas as datas em que o aluno treinou
     */
    public List<LocalDate> getDatasTreinadas(UUID alunoId) {
        return realizadoRepo.findByTreinoAlunoId(alunoId)
                .stream()
                .map(TreinoRealizado::getData)
                .toList();
    }

    /**
     * Obter uma sessão de treino específica
     */
    public Optional<TreinoRealizado> obterSessao(UUID treinoRealizadoId) {
        return realizadoRepo.findById(treinoRealizadoId);
    }

    /**
     * Buscar todas as sessões de treino de um aluno
     */
    public List<TreinoRealizado> buscarSessoesPorAluno(UUID alunoId) {
        return realizadoRepo.findByTreinoAlunoId(alunoId);
    }
}
