package com.treino_abc_backend.repository;

import com.treino_abc_backend.entity.Treino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TreinoRepository extends JpaRepository<Treino, UUID> {

    List<Treino> findByAlunoId(UUID alunoId);

    boolean existsByAlunoId(UUID alunoId);
}
