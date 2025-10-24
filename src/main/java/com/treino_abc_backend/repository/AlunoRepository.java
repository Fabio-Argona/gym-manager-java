package com.treino_abc_backend.repository;

import com.treino_abc_backend.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AlunoRepository extends JpaRepository<Aluno, UUID> {
    Optional<Aluno> findByEmail(String email);
    boolean existsByEmail(String email);
}
