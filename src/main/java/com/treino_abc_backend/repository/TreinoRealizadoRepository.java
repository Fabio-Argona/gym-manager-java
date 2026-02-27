package com.treino_abc_backend.repository;

import com.treino_abc_backend.entity.TreinoRealizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TreinoRealizadoRepository extends JpaRepository<TreinoRealizado, UUID> {
    /**
     * Busca todas as sessões de treino de um aluno
     */
    @Query("SELECT tr FROM TreinoRealizado tr WHERE tr.alunoId = :alunoId ORDER BY tr.data DESC")
    List<TreinoRealizado> findByTreinoAlunoId(@Param("alunoId") UUID alunoId);
}
