package com.treino_abc_backend.service;

import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.repository.ExercicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExercicioService {

    private final ExercicioRepository repository;

    public ExercicioService(ExercicioRepository repository) {
        this.repository = repository;
    }

    public List<Exercicio> getTodos() {
        return repository.findAll();
    }

    public List<Exercicio> getPorAluno(String alunoId) {
        UUID alunoUUID = UUID.fromString(alunoId);
        return repository.findByAlunoId(alunoUUID);
    }

    public Exercicio salvar(Exercicio exercicio) {
        validarAluno(exercicio);
        return repository.save(exercicio);
    }

    public List<Exercicio> salvarTodos(List<Exercicio> exercicios) {
        for (Exercicio e : exercicios) {
            validarAluno(e);
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
}
