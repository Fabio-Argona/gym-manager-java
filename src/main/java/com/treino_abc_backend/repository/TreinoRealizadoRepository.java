package com.treino_abc_backend.repository;

import com.treino_abc_backend.entity.TreinoRealizado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TreinoRealizadoRepository extends JpaRepository<TreinoRealizado, UUID> {
    List<TreinoRealizado> findByTreinoAlunoId(UUID alunoId);
}
