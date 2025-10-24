package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.TreinoGrupoDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.entity.TreinoGrupo;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TreinoGrupoService {

    private final TreinoGrupoRepository grupoRepo;
    private final AlunoRepository alunoRepo;

    public TreinoGrupoService(TreinoGrupoRepository grupoRepo, AlunoRepository alunoRepo) {
        this.grupoRepo = grupoRepo;
        this.alunoRepo = alunoRepo;
    }

    public TreinoGrupoDTO criar(TreinoGrupoDTO dto) {
        Aluno aluno = alunoRepo.findById(dto.getAlunoId())
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));

        TreinoGrupo grupo = new TreinoGrupo();
        grupo.setNome(dto.getNome());
        grupo.setAluno(aluno);

        TreinoGrupo salvo = grupoRepo.save(grupo);
        return toDTO(salvo);
    }

    public List<TreinoGrupoDTO> listarPorAluno(UUID alunoId) {
        return grupoRepo.findByAluno_Id(alunoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void remover(UUID id) {
        if (!grupoRepo.existsById(id)) {
            throw new IllegalArgumentException("Grupo não encontrado para remoção");
        }
        grupoRepo.deleteById(id);
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
}
