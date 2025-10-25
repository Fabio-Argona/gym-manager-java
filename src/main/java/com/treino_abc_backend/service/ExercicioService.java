package com.treino_abc_backend.service;

import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.entity.TreinoGrupo;
import com.treino_abc_backend.repository.ExercicioRepository;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExercicioService {

    private final ExercicioRepository repository;
    private final TreinoGrupoRepository grupoRepository;

    public ExercicioService(ExercicioRepository repository, TreinoGrupoRepository grupoRepository) {
        this.repository = repository;
        this.grupoRepository = grupoRepository;
    }

    public List<Exercicio> getTodos() {
        return repository.findAll();
    }

    public List<Exercicio> getPorAluno(String alunoId) {
        UUID alunoUUID = UUID.fromString(alunoId);
        return repository.findByAlunoIdAndAtivoTrue(alunoUUID); // ✅ apenas ativos
    }

    public Exercicio salvar(Exercicio exercicio) {
        validarAluno(exercicio);
        if (exercicio.getAtivo() == null) {
            exercicio.setAtivo(true); // ✅ padrão ativo
        }
        return repository.save(exercicio);
    }

    public List<Exercicio> salvarTodos(List<Exercicio> exercicios) {
        for (Exercicio e : exercicios) {
            validarAluno(e);
            if (e.getAtivo() == null) {
                e.setAtivo(true);
            }
        }
        return repository.saveAll(exercicios);
    }

    public Optional<Exercicio> atualizar(Exercicio novo, UUID alunoId) {
        Optional<Exercicio> existente = repository.findById(novo.getId());
        if (existente.isEmpty()) return Optional.empty();

        Exercicio exercicio = existente.get();
        if (!exercicio.getAlunoId().equals(alunoId)) {
            throw new RuntimeException("Você não pode alterar exercícios de outro aluno.");
        }

        exercicio.setNome(novo.getNome());
        exercicio.setGrupoMuscular(novo.getGrupoMuscular());
        exercicio.setSeries(novo.getSeries());
        exercicio.setRepMin(novo.getRepMin());
        exercicio.setRepMax(novo.getRepMax());
        exercicio.setPesoInicial(novo.getPesoInicial());
        exercicio.setObservacao(novo.getObservacao());
        exercicio.setAtivo(novo.getAtivo()); // ✅ atualiza status

        return Optional.of(repository.save(exercicio));
    }

    public void deletar(String id, UUID alunoId) {
        Exercicio exercicio = repository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Exercício não encontrado."));

        if (!exercicio.getAlunoId().equals(alunoId)) {
            throw new RuntimeException("Você não pode deletar exercícios de outro aluno.");
        }

        repository.deleteById(exercicio.getId());
    }

    public Exercicio adicionarAoTreino(Exercicio exercicio) {
        validarAluno(exercicio);

        if (exercicio.getGrupoId() != null) {
            TreinoGrupo grupo = grupoRepository.findById(exercicio.getGrupoId())
                    .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));
            exercicio.setGrupo(grupo);
        }

        if (exercicio.getAtivo() == null) {
            exercicio.setAtivo(true);
        }

        return repository.save(exercicio);
    }


    public void removerDoTreino(String id, UUID alunoId) {
        deletar(id, alunoId);
    }

    private void validarAluno(Exercicio exercicio) {
        if (exercicio.getAlunoId() == null) {
            throw new RuntimeException("Exercício deve ter um aluno associado.");
        }
    }

    public Optional<Exercicio> atualizarStatus(UUID id, boolean ativo) {
        Optional<Exercicio> exercicioOpt = repository.findById(id);
        if (exercicioOpt.isEmpty()) return Optional.empty();

        Exercicio exercicio = exercicioOpt.get();
        exercicio.setAtivo(ativo);
        return Optional.of(repository.save(exercicio));
    }
}
