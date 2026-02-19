package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.*;
import com.treino_abc_backend.entity.*;
import com.treino_abc_backend.enums.StatusTreino;
import com.treino_abc_backend.enums.StatusExecucaoExercicio;
import com.treino_abc_backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class TreinoService {

    private final TreinoExercicioAlunoRepository treinoRepo;
    private final AlunoRepository alunoRepo;
    private final ExercicioRepository exercicioRepo;
    private final TreinoGrupoRepository grupoRepo;
    private final TreinoRepository repository;

    public TreinoService(
            TreinoExercicioAlunoRepository treinoRepo,
            AlunoRepository alunoRepo,
            ExercicioRepository exercicioRepo,
            TreinoGrupoRepository grupoRepo,
            TreinoRepository repository
    ) {
        this.treinoRepo = treinoRepo;
        this.alunoRepo = alunoRepo;
        this.exercicioRepo = exercicioRepo;
        this.grupoRepo = grupoRepo;
        this.repository = repository;
    }

    // ---------------------------------------------------------------
    // LISTAR GRUPOS COM EXERCÍCIOS
    // ---------------------------------------------------------------
    public List<TreinoGrupoDTO> listarGruposComExercicios(UUID alunoId) {

        List<TreinoExercicioAluno> lista =
                treinoRepo.findByGrupo_Aluno_Id(alunoId);

        Map<UUID, TreinoGrupoDTO> grupos = new LinkedHashMap<>();

        for (TreinoExercicioAluno t : lista) {

            if (t.getGrupo() == null || t.getExercicio() == null) continue;

            UUID grupoId = t.getGrupo().getId();

            TreinoGrupoDTO grupoDTO = grupos.computeIfAbsent(grupoId, id -> {
                TreinoGrupoDTO novo = new TreinoGrupoDTO();
                novo.setId(id);
                novo.setNome(t.getGrupo().getNome());
                novo.setExercicios(new ArrayList<>());
                return novo;
            });

            TreinoExercicioDTO ex = new TreinoExercicioDTO();
            ex.setGrupoId(grupoId);
            ex.setNomeExercicio(t.getExercicio().getNome());
            ex.setGrupoMuscular(t.getExercicio().getGrupoMuscular());
            ex.setSeries(t.getExercicio().getSeries());
            ex.setRepeticoes(t.getExercicio().getRepeticoes());
            ex.setPesoInicial(t.getExercicio().getPesoInicial());
            ex.setObservacao(t.getObservacao());

            grupoDTO.getExercicios().add(ex);
        }

        return new ArrayList<>(grupos.values());
    }


    // ---------------------------------------------------------------
    // ADICIONAR TREINO
    // ---------------------------------------------------------------
    public TreinoExercicioAluno adicionar(UUID alunoId, UUID grupoId, UUID exercicioId,
                                          String diaSemana, Integer ordem, String observacao) {

        alunoRepo.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));

        Exercicio exercicio = exercicioRepo.findById(exercicioId)
                .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado"));

        TreinoGrupo grupo = grupoRepo.findById(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo de treino não encontrado"));

        TreinoExercicioAluno treino = new TreinoExercicioAluno();
        treino.setAlunoId(alunoId);
        treino.setExercicio(exercicio);
        treino.setGrupo(grupo);
        treino.setDiaSemana(diaSemana);
        treino.setOrdem(ordem != null ? ordem : 1);
        treino.setObservacao(observacao != null ? observacao : "");
        treino.setNomeExercicio(exercicio.getNome());

        return treinoRepo.save(treino);
    }

    // ---------------------------------------------------------------
    // ATUALIZAR POR ID
    // ---------------------------------------------------------------
    public TreinoExercicioAluno atualizar(UUID id, UUID exercicioId, String diaSemana,
                                          Integer ordem, String observacao) {

        TreinoExercicioAluno treino = treinoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Treino não encontrado"));

        Exercicio exercicio = exercicioRepo.findById(exercicioId)
                .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado"));

        treino.setExercicio(exercicio);
        treino.setNomeExercicio(exercicio.getNome());

        if (diaSemana != null) treino.setDiaSemana(diaSemana);
        if (ordem != null) treino.setOrdem(ordem);
        if (observacao != null) treino.setObservacao(observacao);

        return treinoRepo.save(treino);
    }

    // ---------------------------------------------------------------
    // ATUALIZAR VIA DTO
    // ---------------------------------------------------------------
    public TreinoExercicioAluno atualizarViaDTO(UUID id, TreinoDTO dto) {
        TreinoExercicioAluno treino = treinoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Treino não encontrado"));

        Exercicio exercicio = exercicioRepo.findById(dto.getExercicioId())
                .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado"));

        exercicio.setSeries(dto.getSeries());
        exercicio.setRepeticoes(dto.getRepeticoes());
        exercicio.setPesoInicial(dto.getPesoInicial());
        exercicioRepo.save(exercicio);

        treino.setExercicio(exercicio);
        treino.setDiaSemana(dto.getDiaSemana());
        treino.setOrdem(dto.getOrdem());
        treino.setObservacao(dto.getObservacao());
        treino.setNomeExercicio(exercicio.getNome());

        return treinoRepo.save(treino);
    }

    // ---------------------------------------------------------------
    // LISTAR POR ALUNO
    // ---------------------------------------------------------------
    public List<TreinoExercicioAluno> listarPorAluno(UUID alunoId) {
        return treinoRepo.findByAlunoId(alunoId);
    }

    // ---------------------------------------------------------------
    // REMOVER
    // ---------------------------------------------------------------
    public void remover(UUID id) {
        if (!treinoRepo.existsById(id)) {
            throw new IllegalArgumentException("Treino não encontrado");
        }
        treinoRepo.deleteById(id);
    }

    // ---------------------------------------------------------------
    // FICHA DO ALUNO AGRUPADA POR DIA
    // ---------------------------------------------------------------
    public Map<String, List<FichaTreinoDTO>> fichaPorAluno(UUID alunoId) {
        List<TreinoExercicioAluno> treinos = treinoRepo.findByAlunoId(alunoId);

        return treinos.stream().collect(Collectors.groupingBy(
                TreinoExercicioAluno::getDiaSemana,
                Collectors.mapping(t -> new FichaTreinoDTO(
                        t.getExercicio().getNome(),
                        t.getExercicio().getGrupoMuscular(),
                        t.getExercicio().getSeries(),
                        t.getExercicio().getRepeticoes(),
                        t.getExercicio().getPesoInicial(),
                        t.getObservacao()
                ), Collectors.toList())
        ));
    }

    // ---------------------------------------------------------------
    // EXCLUIR GRUPO SEM APAGAR EXERCÍCIOS
    // ---------------------------------------------------------------
    public void excluirGrupoComExercicios(UUID grupoId) {
        List<TreinoExercicioAluno> treinamentos = treinoRepo.findByGrupo_Id(grupoId);
        for (TreinoExercicioAluno t : treinamentos) {
            t.setGrupo(null);
        }
        treinoRepo.saveAll(treinamentos);

        List<Exercicio> exercicios = exercicioRepo.findByGrupo_Id(grupoId);
        for (Exercicio e : exercicios) {
            e.setAtivo(false);
            e.setGrupo(null);
        }
        exercicioRepo.saveAll(exercicios);

        if (!grupoRepo.existsById(grupoId)) {
            throw new IllegalArgumentException("Grupo não encontrado");
        }

        grupoRepo.deleteById(grupoId);
    }

    // ---------------------------------------------------------------
    // ATUALIZAR STATUS DO TREINO MANUALMENTE
    // ---------------------------------------------------------------
    public Treino atualizarStatus(UUID treinoId, StatusTreino novoStatus, UUID alunoId) {
        Treino treino = repository.findById(treinoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Treino não encontrado"));

        if (!treino.getAlunoId().equals(alunoId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode alterar treino de outro aluno");
        }

        treino.setStatus(novoStatus);
        return repository.save(treino);
    }

    // ---------------------------------------------------------------
    // CALCULAR STATUS DO TREINO AUTOMATICAMENTE
    // ---------------------------------------------------------------
    public StatusTreino calcularStatusTreino(UUID grupoId) {

        List<TreinoExercicioAluno> exercicios =
                treinoRepo.findByGrupo_Id(grupoId);

        if (exercicios.isEmpty()) {
            return StatusTreino.AGENDADO;
        }

        boolean todosRealizados = exercicios.stream()
                .allMatch(e -> e.getStatus() == StatusExecucaoExercicio.REALIZADO);

        boolean algumExecutado = exercicios.stream()
                .anyMatch(e ->
                        e.getStatus() == StatusExecucaoExercicio.EM_EXECUCAO
                                || e.getStatus() == StatusExecucaoExercicio.REALIZADO);

        if (todosRealizados) {
            return StatusTreino.REALIZADO;
        } else if (algumExecutado) {
            return StatusTreino.EXECUCAO_PARCIAL;
        } else {
            return StatusTreino.AGENDADO;
        }
    }



    // ---------------------------------------------------------------
    // ATUALIZAR STATUS AUTOMATICAMENTE COM BASE NOS EXERCÍCIOS
    // ---------------------------------------------------------------
    public Treino atualizarStatusAutomatico(UUID treinoId) {
        Treino treino = repository.findById(treinoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Treino não encontrado"));

        StatusTreino novoStatus = calcularStatusTreino(treinoId);
        treino.setStatus(novoStatus);
        return repository.save(treino);
    }
}
