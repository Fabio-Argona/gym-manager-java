package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.FichaTreinoDTO;
import com.treino_abc_backend.dto.TreinoDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.entity.TreinoExercicioAluno;
import com.treino_abc_backend.entity.TreinoGrupo;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.repository.ExercicioRepository;
import com.treino_abc_backend.repository.TreinoExercicioAlunoRepository;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TreinoService {

    @Autowired
    private TreinoExercicioAlunoRepository treinoRepo;

    @Autowired
    private AlunoRepository alunoRepo;

    @Autowired
    private ExercicioRepository exercicioRepo;

    @Autowired
    private TreinoGrupoRepository grupoRepo;

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

    public List<TreinoDTO> listarDTOsPorGrupo(UUID grupoId) {
        return treinoRepo.findByGrupoId(grupoId).stream()
                .map(treino -> new TreinoDTO(
                        treino.getId(),
                        treino.getGrupo().getId(),
                        treino.getAluno().getId(),
                        treino.getExercicio().getId(),
                        treino.getNomeExercicio() != null && !treino.getNomeExercicio().isBlank()
                                ? treino.getNomeExercicio()
                                : treino.getExercicio().getNome(),
                        treino.getExercicio().getSeries(),
                        treino.getExercicio().getRepMin(),
                        treino.getExercicio().getRepMax(),
                        treino.getExercicio().getPesoInicial(),
                        treino.getDiaSemana(),
                        treino.getOrdem(),
                        treino.getObservacao()
                ))
                .collect(Collectors.toList());
    }


    public TreinoExercicioAluno atualizarViaDTO(UUID id, TreinoDTO dto) {
        TreinoExercicioAluno treino = treinoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Treino não encontrado"));

        Exercicio exercicio = exercicioRepo.findById(dto.getExercicioId())
                .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado"));

        // Atualiza os dados do exercício
        exercicio.setSeries(dto.getSeries());
        exercicio.setRepMin(dto.getRepMin());
        exercicio.setRepMax(dto.getRepMax());
        exercicio.setPesoInicial(dto.getPesoInicial());
        exercicioRepo.save(exercicio);

        // Atualiza os dados do treino
        treino.setExercicio(exercicio);
        treino.setDiaSemana(dto.getDiaSemana());
        treino.setOrdem(dto.getOrdem());
        treino.setObservacao(dto.getObservacao());

        // Salva nome personalizado, se fornecido
        if (dto.getNomeExercicio() != null && !dto.getNomeExercicio().isBlank()) {
            treino.setNomeExercicio(dto.getNomeExercicio());
        }

        return treinoRepo.save(treino);
    }






}
