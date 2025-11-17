package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.*;
import com.treino_abc_backend.entity.*;
import com.treino_abc_backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TreinoService {

    private final TreinoExercicioAlunoRepository treinoRepo;
    private final AlunoRepository alunoRepo;
    private final ExercicioRepository exercicioRepo;
    private final TreinoGrupoRepository grupoRepo;

    public TreinoService(
            TreinoExercicioAlunoRepository treinoRepo,
            AlunoRepository alunoRepo,
            ExercicioRepository exercicioRepo,
            TreinoGrupoRepository grupoRepo
    ) {
        this.treinoRepo = treinoRepo;
        this.alunoRepo = alunoRepo;
        this.exercicioRepo = exercicioRepo;
        this.grupoRepo = grupoRepo;
    }

    public List<TreinoGrupoDTO> listarGruposComExercicios(UUID alunoId) {
        List<TreinoExercicioAluno> lista = treinoRepo.findByAlunoId(alunoId);
        Map<UUID, TreinoGrupoDTO> grupos = new LinkedHashMap<>();

        for (TreinoExercicioAluno t : lista) {
            UUID grupoId = t.getGrupo().getId();
            TreinoGrupoDTO grupoDTO = grupos.computeIfAbsent(grupoId, id -> {
                TreinoGrupoDTO novo = new TreinoGrupoDTO();
                novo.setId(id);
                novo.setNome(t.getGrupo().getNome());
                novo.setExercicios(new ArrayList<>());
                return novo;
            });

            TreinoExercicioDTO ex = new TreinoExercicioDTO();
            ex.setNomeExercicio(t.getNomeExercicio());
            ex.setGrupoMuscular(t.getExercicio().getGrupoMuscular());
            ex.setSeries(t.getExercicio().getSeries());
            ex.setRepMin(t.getExercicio().getRepMin());
            ex.setRepMax(t.getExercicio().getRepMax());
            ex.setPesoInicial(t.getExercicio().getPesoInicial());
            ex.setObservacao(t.getObservacao());

            grupoDTO.getExercicios().add(ex);
        }

        return new ArrayList<>(grupos.values());
    }

    public TreinoExercicioAluno adicionar(UUID alunoId, UUID grupoId, UUID exercicioId, String diaSemana, Integer ordem, String observacao) {
        Aluno aluno = alunoRepo.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        Exercicio exercicio = exercicioRepo.findById(exercicioId)
                .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado"));
        TreinoGrupo grupo = grupoRepo.findById(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo de treino não encontrado"));

        TreinoExercicioAluno treino = new TreinoExercicioAluno();
        treino.setAluno(aluno);
        treino.setExercicio(exercicio);
        treino.setGrupo(grupo);
        treino.setOrdem(ordem != null ? ordem : 1);
        treino.setObservacao(observacao != null ? observacao : "");
        treino.setDiaSemana(diaSemana);

        return treinoRepo.save(treino);
    }

    public TreinoExercicioAluno atualizar(UUID id, UUID exercicioId, String diaSemana, Integer ordem, String observacao) {
        TreinoExercicioAluno treino = treinoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Treino não encontrado"));
        Exercicio exercicio = exercicioRepo.findById(exercicioId)
                .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado"));

        treino.setExercicio(exercicio);
        if (ordem != null) treino.setOrdem(ordem);
        if (observacao != null) treino.setObservacao(observacao);
        if (diaSemana != null) treino.setDiaSemana(diaSemana);

        return treinoRepo.save(treino);
    }

    public TreinoExercicioAluno atualizarViaDTO(UUID id, TreinoDTO dto) {
        TreinoExercicioAluno treino = treinoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Treino não encontrado"));
        Exercicio exercicio = exercicioRepo.findById(dto.getExercicioId())
                .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado"));

        exercicio.setSeries(dto.getSeries());
        exercicio.setRepMin(dto.getRepMin());
        exercicio.setRepMax(dto.getRepMax());
        exercicio.setPesoInicial(dto.getPesoInicial());
        exercicioRepo.save(exercicio);

        treino.setExercicio(exercicio);
        treino.setDiaSemana(dto.getDiaSemana());
        treino.setOrdem(dto.getOrdem());
        treino.setObservacao(dto.getObservacao());

        if (dto.getNomeExercicio() != null && !dto.getNomeExercicio().isBlank()) {
            treino.setNomeExercicio(dto.getNomeExercicio());
        }

        return treinoRepo.save(treino);
    }

    public List<TreinoExercicioAluno> listarPorAluno(UUID alunoId) {
        return treinoRepo.findByAlunoId(alunoId);
    }

    public void remover(UUID id) {
        if (!treinoRepo.existsById(id)) {
            throw new IllegalArgumentException("Treino não encontrado para remoção");
        }
        treinoRepo.deleteById(id);
    }

    public Map<String, List<FichaTreinoDTO>> fichaPorAluno(UUID alunoId) {
        List<TreinoExercicioAluno> treinos = treinoRepo.findByAlunoId(alunoId);

        return treinos.stream()
                .collect(Collectors.groupingBy(
                        TreinoExercicioAluno::getDiaSemana,
                        Collectors.mapping(treino -> new FichaTreinoDTO(
                                treino.getExercicio().getNome(),
                                treino.getExercicio().getGrupoMuscular(),
                                treino.getExercicio().getSeries(),
                                treino.getExercicio().getRepMin(),
                                treino.getExercicio().getRepMax(),
                                treino.getExercicio().getPesoInicial(),
                                treino.getObservacao()
                        ), Collectors.toList())
                ));
    }

    public void excluirGrupoComExercicios(UUID grupoId) {
        // 1. Buscar todos os vínculos de exercícios com o grupo
        List<TreinoExercicioAluno> vinculados = treinoRepo.findByGrupo_Id(grupoId);

        // 2. Excluir vínculos
        treinoRepo.deleteAll(vinculados);

        // 3. Excluir o grupo
        if (!grupoRepo.existsById(grupoId)) {
            throw new IllegalArgumentException("Grupo não encontrado");
        }
        grupoRepo.deleteById(grupoId);
    }





}
