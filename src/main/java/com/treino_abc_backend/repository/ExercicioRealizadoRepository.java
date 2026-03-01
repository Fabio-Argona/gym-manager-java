package com.treino_abc_backend.repository;

import com.treino_abc_backend.entity.ExercicioRealizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExercicioRealizadoRepository extends JpaRepository<ExercicioRealizado, UUID> {

    /**
     * Busca todos os exercícios realizados de um aluno em uma data específica
     */
    @Query("SELECT er FROM ExercicioRealizado er " +
           "WHERE er.treinoRealizado.alunoId = :alunoId " +
           "AND er.dataSessao = :data " +
           "ORDER BY er.criadoEm DESC")
    List<ExercicioRealizado> findByAlunoIdAndData(@Param("alunoId") UUID alunoId, @Param("data") LocalDate data);

    /**
     * Busca todos os exercícios realizados de um aluno em um período
     */
    @Query("SELECT er FROM ExercicioRealizado er " +
           "WHERE er.treinoRealizado.alunoId = :alunoId " +
           "AND er.dataSessao BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY er.dataSessao DESC, er.criadoEm DESC")
    List<ExercicioRealizado> findByAlunoIdAndDataRange(@Param("alunoId") UUID alunoId,
                                                       @Param("dataInicio") LocalDate dataInicio,
                                                       @Param("dataFim") LocalDate dataFim);

    /**
     * Busca exercícios realizados de um exercício específico
     */
    @Query("SELECT er FROM ExercicioRealizado er " +
           "WHERE er.treinoRealizado.alunoId = :alunoId " +
           "AND er.exercicio.id = :exercicioId " +
           "ORDER BY er.dataSessao DESC")
    List<ExercicioRealizado> findByAlunoIdAndExercicioId(@Param("alunoId") UUID alunoId,
                                                         @Param("exercicioId") UUID exercicioId);

    /**
     * Busca exercícios realizados de um exercício em um período (para gráficos de progressão)
     */
    @Query("SELECT er FROM ExercicioRealizado er " +
           "WHERE er.treinoRealizado.alunoId = :alunoId " +
           "AND er.exercicio.id = :exercicioId " +
           "AND er.dataSessao BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY er.dataSessao ASC")
    List<ExercicioRealizado> findProgressao(@Param("alunoId") UUID alunoId,
                                           @Param("exercicioId") UUID exercicioId,
                                           @Param("dataInicio") LocalDate dataInicio,
                                           @Param("dataFim") LocalDate dataFim);

    /**
     * Busca por TreinoRealizado (sessão de treino)
     */
    List<ExercicioRealizado> findByTreinoRealizadoId(UUID treinoRealizadoId);

    /**
     * Busca todos os exercícios realizados de um aluno (sem filtro de data)
     */
    @Query("SELECT er FROM ExercicioRealizado er " +
           "WHERE er.treinoRealizado.alunoId = :alunoId " +
           "ORDER BY er.dataSessao DESC, er.criadoEm DESC")
    List<ExercicioRealizado> findByAlunoId(@Param("alunoId") UUID alunoId);
}
