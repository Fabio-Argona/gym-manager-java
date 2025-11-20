package com.treino_abc_backend.repository;

import com.treino_abc_backend.entity.EvolucaoMedidas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EvolucaoMedidasRepository extends JpaRepository<EvolucaoMedidas, UUID> {

    List<EvolucaoMedidas> findByAlunoIdOrderByDataAsc(UUID alunoId);
}
