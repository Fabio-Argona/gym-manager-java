package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.ExercicioDTO;
import com.treino_abc_backend.dto.ExercicioEdicaoDTO;
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
import java.util.stream.Collectors;

@Service
public class ExercicioService {

    private final ExercicioRepository repository;
    private final TreinoGrupoRepository grupoRepository;

    public ExercicioService(ExercicioRepository repository, TreinoGrupoRepository grupoRepository) {
        this.repository = repository;
        this.grupoRepository = grupoRepository;
    }

    public List<ExercicioDTO> getPorAluno(UUID alunoId) {
        return repository.findByAlunoIdAndGrupoIsNotNullAndAtivoTrue(alunoId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Exercicio criarExercicio(ExercicioDTO dto) {
        Exercicio e = new Exercicio();
        e.setNome(dto.getNome());
        e.setGrupoMuscular(dto.getGrupoMuscular());
        e.setSeries(dto.getSeries());
        e.setRepeticoes(dto.getRepeticoes());
        e.setPesoInicial(dto.getPesoInicial());
        e.setObservacao(dto.getObservacao());
        e.setAtivo(dto.isAtivo());
        e.setAlunoId(dto.getAlunoId());
        e.setDataCriacao(LocalDateTime.now());

        if (dto.getGrupoId() != null) {
            TreinoGrupo grupo = grupoRepository.findById(dto.getGrupoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo não encontrado"));
            e.setGrupo(grupo);
        }

        return repository.save(e);
    }

    public Exercicio atualizar(UUID id, ExercicioEdicaoDTO dto, UUID alunoId) {
        Exercicio existente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado"));

        if (!existente.getAlunoId().equals(alunoId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode alterar exercícios de outro aluno");
        }

        existente.setNome(dto.getNome());
        existente.setGrupoMuscular(dto.getGrupoMuscular());
        existente.setSeries(dto.getSeries());
        existente.setRepeticoes(dto.getRepeticoes());
        existente.setPesoInicial(dto.getPesoInicial());
        existente.setObservacao(dto.getObservacao());
        existente.setAtivo(dto.isAtivo());

        if (dto.getGrupoId() != null) {
            TreinoGrupo grupo = grupoRepository.findById(dto.getGrupoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo não encontrado"));
            existente.setGrupo(grupo);
        } else {
            existente.setGrupo(null);
        }

        return repository.save(existente);
    }

    public void deletar(UUID id, UUID alunoId) {
        Exercicio e = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado"));

        if (!e.getAlunoId().equals(alunoId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode deletar exercícios de outro aluno");
        }

        repository.deleteById(id);
    }

    public List<ExercicioDTO> getEvolucaoPorAluno(UUID alunoId) {
        return repository.findByAlunoIdAndGrupoIsNotNullAndAtivoTrue(alunoId)
                .stream()
                .sorted((a, b) -> a.getDataCriacao().compareTo(b.getDataCriacao()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ExercicioDTO toDTO(Exercicio e) {
        ExercicioDTO dto = new ExercicioDTO();
        dto.setId(e.getId());
        dto.setNome(e.getNome());
        dto.setGrupoMuscular(e.getGrupoMuscular());
        dto.setSeries(e.getSeries());
        dto.setRepeticoes(e.getRepeticoes());
        dto.setPesoInicial(e.getPesoInicial());
        dto.setObservacao(e.getObservacao());
        dto.setAlunoId(e.getAlunoId());
        dto.setAtivo(e.isAtivo());
        dto.setDataCriacao(e.getDataCriacao());
        dto.setGrupoId(e.getGrupoId());
        return dto;
    }

    public Exercicio adicionarAoTreino(Exercicio e) {
        if (e.getAlunoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercício deve ter um aluno associado");
        }

        if (e.getGrupoId() != null) {
            TreinoGrupo grupo = grupoRepository.findById(e.getGrupoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo não encontrado"));
            e.setGrupo(grupo);
        }

        e.setDataCriacao(LocalDateTime.now());
        return repository.save(e);
    }

    public void removerDoTreino(String id, UUID alunoId) {
        deletar(UUID.fromString(id), alunoId);
    }

    public Optional<Exercicio> atualizarStatus(UUID id, boolean ativo) {
        Exercicio e = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado"));

        e.setAtivo(ativo);
        if (!ativo) e.setGrupo(null);

        return Optional.of(repository.save(e));
    }
}
