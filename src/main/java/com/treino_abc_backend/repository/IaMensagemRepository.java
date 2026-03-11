package com.treino_abc_backend.repository;

import com.treino_abc_backend.entity.IaMensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IaMensagemRepository extends JpaRepository<IaMensagem, UUID> {

    /**
     * Retorna as últimas 10 mensagens do aluno em ordem cronológica,
     * para ser usado como contexto no prompt do Groq (Llama 3.3).
     */
    List<IaMensagem> findTop10ByAlunoIdOrderByCriadoEmAsc(UUID alunoId);
}
