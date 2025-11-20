package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.FichaTreinoDTO;
import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.entity.TreinoExercicioAluno;
import com.treino_abc_backend.repository.TreinoExercicioAlunoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TreinoServiceTest {

    private TreinoExercicioAlunoRepository treinoRepo;
    private TreinoService treinoService;

    @BeforeEach
    void setup() {
        treinoRepo = Mockito.mock(TreinoExercicioAlunoRepository.class);
        treinoService = new TreinoService(treinoRepo, null, null, null);
    }

    @Test
    void testFichaPorAluno() {
        UUID alunoId = UUID.randomUUID();

        // Criar exercícios
        Exercicio ex1 = new Exercicio();
        ex1.setNome("Supino");
        ex1.setGrupoMuscular("Peito");
        ex1.setSeries(3);
        ex1.setRepeticoes(12);
        ex1.setPesoInicial(40.0);

        Exercicio ex2 = new Exercicio();
        ex2.setNome("Agachamento");
        ex2.setGrupoMuscular("Perna");
        ex2.setSeries(4);
        ex2.setRepeticoes(10);
        ex2.setPesoInicial(50.0);

        // Criar treinos
        TreinoExercicioAluno treino1 = new TreinoExercicioAluno();
        treino1.setExercicio(ex1);
        treino1.setDiaSemana("Segunda-feira");
        treino1.setObservacao("Foco no peito");

        TreinoExercicioAluno treino2 = new TreinoExercicioAluno();
        treino2.setExercicio(ex2);
        treino2.setDiaSemana("Segunda-feira");
        treino2.setObservacao("Perna pesada");

        List<TreinoExercicioAluno> treinos = Arrays.asList(treino1, treino2);

        // Mock do repositório
        when(treinoRepo.findByAlunoId(alunoId)).thenReturn(treinos);

        // Executar o método
        Map<String, List<FichaTreinoDTO>> ficha = treinoService.fichaPorAluno(alunoId);

        // Testar o resultado
        assertEquals(1, ficha.size()); // Apenas uma chave: "Segunda-feira"
        assertEquals(2, ficha.get("Segunda-feira").size()); // Dois exercícios

        FichaTreinoDTO dto1 = ficha.get("Segunda-feira").get(0);
        assertEquals("Supino", dto1.getExercicio());
        assertEquals("Peito", dto1.getGrupoMuscular());
        assertEquals(3, dto1.getSeries());
        assertEquals(12, dto1.repeticoes());
        assertEquals(40.0, dto1.getPesoInicial());
        assertEquals("Foco no peito", dto1.getObservacao());
    }
}
