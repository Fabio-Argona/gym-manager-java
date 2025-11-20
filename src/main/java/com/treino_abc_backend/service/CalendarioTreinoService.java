package com.treino_abc_backend.service;

import com.treino_abc_backend.entity.TreinoExercicioAluno;
import com.treino_abc_backend.repository.TreinoExercicioAlunoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class CalendarioTreinoService {

    private final TreinoExercicioAlunoRepository treinoRepo;

    public CalendarioTreinoService(TreinoExercicioAlunoRepository treinoRepo) {
        this.treinoRepo = treinoRepo;
    }

    public Map<Integer, List<String>> gerarCalendario(UUID alunoId, YearMonth mesAno) {

        LocalDate inicio = mesAno.atDay(1);
        LocalDate fim = mesAno.atEndOfMonth();

        List<TreinoExercicioAluno> treinos = treinoRepo.findByAlunoId(alunoId);

        Map<LocalDate, List<String>> calendario = new HashMap<>();

        for (TreinoExercicioAluno treino : treinos) {
            treino.getDatasTreinadas().stream()
                    .filter(data -> !data.isBefore(inicio) && !data.isAfter(fim))
                    .forEach(data -> calendario
                            .computeIfAbsent(data, k -> new ArrayList<>())
                            .add(treino.getExercicio().getNome()));
        }

        Map<Integer, List<String>> calendarioMes = new TreeMap<>();
        calendario.forEach((data, nomes) ->
                calendarioMes.put(data.getDayOfMonth(), nomes)
        );

        return calendarioMes;
    }
}
