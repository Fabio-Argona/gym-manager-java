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
     * Registrar sessão de treino a partir do ID do grupo.
     * O frontend envia o grupoId (TreinoGrupo.id). Buscamos o primeiro
     * TreinoExercicioAluno desse grupo para criar o vínculo.
     */
    public TreinoRealizado registrarPorGrupo(UUID grupoId, LocalDate data) {
        List<TreinoExercicioAluno> exerciciosDoGrupo = treinoRepo.findByGrupo_Id(grupoId);
        if (exerciciosDoGrupo.isEmpty()) {
            throw new IllegalArgumentException("Treino não encontrado para o grupo informado");
        }
        TreinoExercicioAluno treino = exerciciosDoGrupo.get(0);
        UUID alunoId = treino.getAlunoId();

        // Verificar se já existe uma sessão para este grupo nesta data
        Optional<TreinoRealizado> existente = realizadoRepo.findByTreinoAlunoId(alunoId)
                .stream()
                .filter(tr -> tr.getTreino().getGrupo() != null
                        && tr.getTreino().getGrupo().getId().equals(grupoId)
                        && tr.getData().equals(data))
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
     * Registrar que o treino foi feito em determinada data (por TreinoExercicioAluno ID).
     * Mantido para compatibilidade com chamadas internas.
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
