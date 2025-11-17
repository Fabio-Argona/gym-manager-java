package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.ExercicioDTO;
import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.entity.TreinoGrupo;
import com.treino_abc_backend.repository.ExercicioRepository;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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
        return repository.findByAlunoIdAndGrupoIsNotNullAndAtivoTrue(alunoUUID);
    }

    public Exercicio salvar(Exercicio exercicio) {
        validarAluno(exercicio);

        if (exercicio.getGrupoId() != null) {
            TreinoGrupo grupo = grupoRepository.findById(exercicio.getGrupoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo não encontrado"));
            exercicio.setGrupo(grupo);
        }

        if (exercicio.getDataCriacao() == null) {
            exercicio.setDataCriacao(LocalDateTime.now());
        }

        return repository.save(exercicio);
    }

    public Exercicio criarExercicio(ExercicioDTO dto) {
        Exercicio exercicio = new Exercicio();
        exercicio.setNome(dto.getNome());
        exercicio.setGrupoMuscular(dto.getGrupoMuscular());
        exercicio.setSeries(dto.getSeries());
        exercicio.setRepMin(dto.getRepMin());
        exercicio.setRepMax(dto.getRepMax());
        exercicio.setPesoInicial(dto.getPesoInicial());
        exercicio.setObservacao(dto.getObservacao());
        exercicio.setAtivo(dto.isAtivo());
        exercicio.setAlunoId(dto.getAlunoId());
        exercicio.setDataCriacao(LocalDateTime.now());

        if (dto.getGrupoId() != null) {
            TreinoGrupo grupo = grupoRepository.findById(dto.getGrupoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo não encontrado"));
            exercicio.setGrupo(grupo);
        }

        return repository.save(exercicio);
    }


    public List<Exercicio> salvarTodos(List<Exercicio> exercicios) {
        for (Exercicio e : exercicios) {
            validarAluno(e);
            if (e.getDataCriacao() == null) {
                e.setDataCriacao(LocalDateTime.now());
            }
        }
        return repository.saveAll(exercicios);
    }

    public Optional<Exercicio> atualizar(Exercicio novo, UUID alunoId) {
        Exercicio existente = repository.findById(novo.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado"));

        if (!existente.getAlunoId().equals(alunoId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode alterar exercícios de outro aluno");
        }

        existente.setNome(novo.getNome());
        existente.setGrupoMuscular(novo.getGrupoMuscular());
        existente.setSeries(novo.getSeries());
        existente.setRepMin(novo.getRepMin());
        existente.setRepMax(novo.getRepMax());
        existente.setPesoInicial(novo.getPesoInicial());
        existente.setObservacao(novo.getObservacao());
        existente.setAtivo(novo.isAtivo());

        if (novo.getGrupoId() != null) {
            TreinoGrupo grupo = grupoRepository.findById(novo.getGrupoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo não encontrado"));
            existente.setGrupo(grupo);
        }

        return Optional.of(repository.save(existente));
    }

    public void deletar(String id, UUID alunoId) {
        Exercicio exercicio = repository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado"));

        if (!exercicio.getAlunoId().equals(alunoId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode deletar exercícios de outro aluno");
        }

        repository.deleteById(exercicio.getId());
    }

    public Exercicio adicionarAoTreino(Exercicio exercicio) {
        validarAluno(exercicio);

        if (exercicio.getGrupoId() != null) {
            TreinoGrupo grupo = grupoRepository.findById(exercicio.getGrupoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo não encontrado"));
            exercicio.setGrupo(grupo);
        }

        exercicio.setDataCriacao(LocalDateTime.now());
        return repository.save(exercicio);
    }

    public void removerDoTreino(String id, UUID alunoId) {
        deletar(id, alunoId);
    }

    public Optional<Exercicio> atualizarStatus(UUID id, boolean ativo) {
        Exercicio exercicio = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado"));

        exercicio.setAtivo(ativo);
        if (!ativo) {
            exercicio.setGrupo(null);
        }

        return Optional.of(repository.save(exercicio));
    }


    private void validarAluno(Exercicio exercicio) {
        if (exercicio.getAlunoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercício deve ter um aluno associado");
        }
    }


}
