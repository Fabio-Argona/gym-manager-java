package com.treino_abc_backend.repository;

import com.treino_abc_backend.entity.Exercicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExercicioRepository extends JpaRepository<Exercicio, UUID> {
    List<Exercicio> findByAlunoIdAndAtivoTrue(UUID alunoId);
    List<Exercicio> findByAlunoIdAndGrupoIdIsNotNullAndAtivoTrue(UUID alunoId);



}
