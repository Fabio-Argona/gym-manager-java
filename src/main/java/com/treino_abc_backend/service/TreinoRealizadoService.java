package com.treino_abc_backend.service;

import com.treino_abc_backend.entity.TreinoGrupo;
import com.treino_abc_backend.entity.TreinoRealizado;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import com.treino_abc_backend.repository.TreinoRealizadoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TreinoRealizadoService {

    private final TreinoRealizadoRepository realizadoRepo;
    private final TreinoGrupoRepository grupoRepo;

    public TreinoRealizadoService(TreinoRealizadoRepository realizadoRepo,
                                  TreinoGrupoRepository grupoRepo) {
        this.realizadoRepo = realizadoRepo;
        this.grupoRepo = grupoRepo;
    }

    /**
     * Registrar sessão de treino a partir do ID do grupo.
     * O frontend envia o grupoId (TreinoGrupo.id). Buscamos o primeiro
     * TreinoExercicioAluno desse grupo para criar o vínculo.
     */
    public TreinoRealizado registrarPorGrupo(UUID grupoId, LocalDate data) {
        TreinoGrupo grupo = grupoRepo.findById(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));
        UUID alunoId = grupo.getAluno().getId();

        // Verificar se já existe uma sessão para este grupo nesta data
        Optional<TreinoRealizado> existente = realizadoRepo.findByTreinoAlunoId(alunoId)
                .stream()
                .filter(tr -> tr.getGrupo().getId().equals(grupoId)
                        && tr.getData().equals(data))
                .findFirst();

        if (existente.isPresent()) {
            return existente.get();
        }

        TreinoRealizado realizado = new TreinoRealizado();
        realizado.setGrupo(grupo);
        realizado.setAlunoId(alunoId);
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

    /**
     * Buscar a sessão mais recente de um grupo específico
     */
    public Optional<TreinoRealizado> buscarUltimaPorGrupo(UUID grupoId) {
        return realizadoRepo.findTopByGrupo_IdOrderByDataDesc(grupoId);
    }
}
