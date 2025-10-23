package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.TreinoGrupoDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.entity.TreinoGrupo;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TreinoGrupoService {

    @Autowired
    private TreinoGrupoRepository grupoRepo;

    @Autowired
    private AlunoRepository alunoRepo;

    public TreinoGrupoDTO criar(TreinoGrupoDTO dto) {
        Aluno aluno = alunoRepo.findById(dto.getAlunoId())
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));

        TreinoGrupo grupo = new TreinoGrupo();
        grupo.setNome(dto.getNome());
        grupo.setAluno(aluno);

        TreinoGrupo salvo = grupoRepo.save(grupo);

        return new TreinoGrupoDTO(
                salvo.getId(),
                aluno.getId(),
                salvo.getNome()
        );
    }


    public List<TreinoGrupoDTO> listarPorAluno(UUID alunoId) {
        return grupoRepo.findByAluno_Id(alunoId).stream()
                .map(grupo -> new TreinoGrupoDTO(
                        grupo.getId(),
                        grupo.getAluno().getId(),
                        grupo.getNome()
                ))
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

        // Atualizar apenas campos não nulos (PATCH)
        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            grupo.setNome(dto.getNome());
        }

        TreinoGrupo salvo = grupoRepo.save(grupo);

        return new TreinoGrupoDTO(
                salvo.getId(),
                salvo.getAluno().getId(),
                salvo.getNome()
        );
    }

}
