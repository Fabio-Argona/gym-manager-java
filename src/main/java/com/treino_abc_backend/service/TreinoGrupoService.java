package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.TreinoGrupoDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.entity.TreinoGrupo;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.repository.ExercicioRepository;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TreinoGrupoService {

    private final TreinoGrupoRepository grupoRepo;
    private final AlunoRepository alunoRepo;
    private final ExercicioRepository exercicioRepo;

    public TreinoGrupoService(TreinoGrupoRepository grupoRepo, AlunoRepository alunoRepo,
                               ExercicioRepository exercicioRepo) {
        this.grupoRepo = grupoRepo;
        this.alunoRepo = alunoRepo;
        this.exercicioRepo = exercicioRepo;
    }

    public TreinoGrupoDTO criar(TreinoGrupoDTO dto) {
        UUID alunoId = dto.getAlunoId();
        Aluno aluno = alunoRepo.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));

        TreinoGrupo grupo = new TreinoGrupo();
        grupo.setNome(dto.getNome());
        grupo.setAluno(aluno);

        TreinoGrupo salvo = grupoRepo.save(grupo);
        return new TreinoGrupoDTO(salvo.getId(), aluno.getId(), salvo.getNome());
    }

    public List<TreinoGrupoDTO> listarPorAluno(UUID alunoId) {
        return grupoRepo.findByAluno_IdAndAtivoTrue(alunoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void remover(UUID id) {
        TreinoGrupo grupo = grupoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado para remoção"));
        grupo.setAtivo(false);
        grupoRepo.save(grupo);
    }

    public TreinoGrupoDTO editar(UUID grupoId, TreinoGrupoDTO dto) {
        TreinoGrupo grupo = grupoRepo.findById(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            grupo.setNome(dto.getNome());
        }

        TreinoGrupo salvo = grupoRepo.save(grupo);
        return toDTO(salvo);
    }

    private TreinoGrupoDTO toDTO(TreinoGrupo grupo) {
        return new TreinoGrupoDTO(
                grupo.getId(),
                grupo.getAluno().getId(),
                grupo.getNome()
        );
    }

    public void excluirGrupoComExercicios(UUID grupoId) {
        // 1. Buscar exercícios vinculados ao grupo
        List<Exercicio> exercicios = exercicioRepo.findByGrupo_Id(grupoId);
        for (Exercicio e : exercicios) {
            e.setAtivo(false);   // exclusão lógica
            e.setGrupo(null);    // quebra vínculo com grupo
            e.setObservacao(
                    (e.getObservacao() == null ? "" : e.getObservacao() + " | ")
                            + "Exercício desativado ao excluir grupo em " + LocalDateTime.now()
            );
        }
        exercicioRepo.saveAll(exercicios);

        // 2. Desativar o grupo (exclusão lógica)
        TreinoGrupo grupo = grupoRepo.findById(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));
        grupo.setAtivo(false);
        grupoRepo.save(grupo);
    }


}
