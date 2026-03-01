package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.ExercicioDTO;
import com.treino_abc_backend.dto.ExercicioEdicaoDTO;
import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.entity.TreinoGrupo;
import com.treino_abc_backend.repository.ExercicioRepository;
import com.treino_abc_backend.repository.TreinoExercicioAlunoRepository;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.treino_abc_backend.entity.TreinoExercicioAluno;
import com.treino_abc_backend.enums.StatusExecucaoExercicio;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExercicioService {

    private final ExercicioRepository repository;
    private final TreinoGrupoRepository grupoRepository;
    private final TreinoExercicioAlunoRepository treinoExercicioAlunoRepository;

    public ExercicioService(ExercicioRepository repository, TreinoGrupoRepository grupoRepository,
            TreinoExercicioAlunoRepository treinoExercicioAlunoRepository) {
        this.repository = repository;
        this.grupoRepository = grupoRepository;
        this.treinoExercicioAlunoRepository = treinoExercicioAlunoRepository;
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

        // Atualiza apenas os campos que vieram preenchidos
        if (dto.getNome() != null) {
            existente.setNome(dto.getNome());
        }

        if (dto.getGrupoMuscular() != null) {
            existente.setGrupoMuscular(dto.getGrupoMuscular());
        }

        existente.setSeries(dto.getSeries());
        existente.setRepeticoes(dto.getRepeticoes());

        // Ajuste do peso inicial: só atualiza se não for null
        if (dto.getPesoInicial() != null) {
            existente.setPesoInicial(dto.getPesoInicial());
        }

        if (dto.getObservacao() != null) {
            existente.setObservacao(dto.getObservacao());
        }

        existente.setAtivo(dto.isAtivo());

        // Ajuste do grupo: só altera se vier um novo grupoId
        if (dto.getGrupoId() != null) {
            TreinoGrupo grupo = grupoRepository.findById(dto.getGrupoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo não encontrado"));
            existente.setGrupo(grupo);
        }
        // Se vier null, mantém o grupo atual (não sobrescreve com null)

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

        // Busca ou cria o registro de status para este aluno/exercício
        TreinoExercicioAluno te = treinoExercicioAlunoRepository
                .findByExercicio_IdAndAlunoId(e.getId(), e.getAlunoId())
                .orElseGet(() -> {
                    TreinoExercicioAluno novoStatus = new TreinoExercicioAluno();
                    novoStatus.setAlunoId(e.getAlunoId());
                    novoStatus.setExercicio(e);
                    novoStatus.setGrupo(e.getGrupo());
                    novoStatus.setStatus(StatusExecucaoExercicio.AGENDADO);
                    novoStatus.setDiaSemana("N/A"); // Valor padrão
                    novoStatus.setOrdem(0); // Valor padrão
                    novoStatus.setNomeExercicio(e.getNome());
                    novoStatus.setSeries(e.getSeries());
                    novoStatus.setRepeticoes(e.getRepeticoes());
                    novoStatus.setPesoInicial(e.getPesoInicial());
                    return treinoExercicioAlunoRepository.save(novoStatus);
                });

        dto.setTreinoExercicioAlunoId(te.getId());

        return dto;
    }

    public Exercicio adicionarAoTreino(Exercicio e) {
        if (e.getAlunoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercício deve ter um aluno associado");
        }

        if (e.getGrupoMuscular() == null || e.getGrupoMuscular().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "grupo_muscular é obrigatório");
        }

        if (e.getGrupoId() != null) {
            TreinoGrupo grupo = grupoRepository.findById(e.getGrupoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo não encontrado"));
            e.setGrupo(grupo);
        }

        e.setDataCriacao(LocalDateTime.now());
        return repository.save(e);
    }
}
