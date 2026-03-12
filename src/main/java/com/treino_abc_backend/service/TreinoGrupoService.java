package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.TreinoExercicioDTO;
import com.treino_abc_backend.dto.TreinoGrupoDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.entity.Exercicio;
import com.treino_abc_backend.entity.TreinoGrupo;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.repository.ExercicioRepository;
import com.treino_abc_backend.repository.TreinoGrupoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TreinoGrupoService {
            /**
             * Sugere grupos de treino, priorizando os existentes no banco.
             * Se faltar grupos, cria novos com repetições padrão 3x10.
             */
            public List<TreinoGrupoDTO> sugerirOuBuscarGrupos(UUID alunoId, int quantidadeGrupos) {
                List<TreinoGrupo> gruposBanco = grupoRepo.findByAluno_IdAndAtivoTrue(alunoId);
                List<TreinoGrupoDTO> gruposDTO = gruposBanco.stream()
                    .map(grupo -> {
                        List<Exercicio> exerciciosBanco = exercicioRepo.findByGrupo_Id(grupo.getId());
                        List<TreinoExercicioDTO> exerciciosDTO = exerciciosBanco.stream()
                            .map(e -> new TreinoExercicioDTO(
                                e.getGrupo() != null ? e.getGrupo().getId() : null,
                                e.getNome(),
                                e.getGrupoMuscular(),
                                3,
                                10,
                                e.getPesoInicial() != null ? e.getPesoInicial() : 0.0,
                                e.getObservacao(),
                                null))
                            .collect(java.util.stream.Collectors.toList());
                        TreinoGrupoDTO dto = new TreinoGrupoDTO();
                        dto.setId(grupo.getId());
                        dto.setAlunoId(alunoId);
                        dto.setNome(grupo.getNome());
                        dto.setExercicios(exerciciosDTO);
                        return dto;
                    })
                    .collect(java.util.stream.Collectors.toList());

                // Se já houver grupos suficientes, apenas retorna e sugere dicas
                if (gruposDTO.size() >= quantidadeGrupos) {
                    // Aqui você pode retornar dicas, por exemplo:
                    // grupo.getNome() + ": Foque na execução correta, aumente carga progressivamente, mantenha descanso entre séries."
                    for (TreinoGrupoDTO grupo : gruposDTO) {
                        grupo.setNome(grupo.getNome() + " | Dica: Foque na execução correta, aumente carga progressivamente, mantenha descanso entre séries.");
                    }
                    return gruposDTO;
                }
                // Se não houver grupos suficientes, completa com novos
                List<TreinoGrupoDTO> novos = sugerirGruposTreino(alunoId, quantidadeGrupos - gruposDTO.size());
                // Ajusta repetições para 3x10
                for (TreinoGrupoDTO grupo : novos) {
                    for (TreinoExercicioDTO ex : grupo.getExercicios()) {
                        ex.setSeries(3);
                        ex.setRepeticoes(10);
                    }
                }
                gruposDTO.addAll(novos);
                return gruposDTO;
            }
        /**
         * Gera grupos de treino (A, B, C, ...) conforme quantidade, cada um com 7 a 8 exercícios.
         */
        public static List<TreinoGrupoDTO> sugerirGruposTreino(UUID alunoId, int quantidadeGrupos) {
            String[] nomesGrupos = {"Peito e Tríceps", "Costas e Bíceps", "Ombro e Pernas", "Core e Cardio", "Glúteo e Posterior"};
            String[][] exerciciosPorGrupo = {
                {"Flexão de braço", "Supino com halteres", "Tríceps banco", "Crucifixo", "Pullover", "Tríceps francês", "Tríceps testa", "Dips"},
                {"Remada unilateral", "Pull-up (barra fixa)", "Rosca direta", "Rosca alternada", "Remada curvada", "Rosca martelo", "Rosca concentrada", "Pulldown"},
                {"Agachamento", "Avanço", "Elevação lateral", "Militar com halteres", "Stiff", "Panturrilha em pé", "Desenvolvimento", "Agachamento sumô"},
                {"Prancha", "Abdominal", "Mountain climber", "Corrida parada", "Bicicleta no solo", "Abdominal oblíquo", "Jumping jacks", "Burpee"},
                {"Glúteo no solo", "Cadeira abdutora", "Stiff", "Agachamento sumô", "Avanço", "Abdução de quadril", "Elevação de quadril", "Passada"}
            };

            List<TreinoGrupoDTO> grupos = new java.util.ArrayList<>();
            for (int i = 0; i < quantidadeGrupos && i < nomesGrupos.length; i++) {
                TreinoGrupoDTO grupo = new TreinoGrupoDTO();
                grupo.setAlunoId(alunoId);
                char letra = (char)('A' + i);
                grupo.setNome("Treino " + letra + " - " + nomesGrupos[i]);
                List<TreinoExercicioDTO> exercicios = new java.util.ArrayList<>();
                for (int j = 0; j < exerciciosPorGrupo[i].length && j < 8; j++) {
                    exercicios.add(new TreinoExercicioDTO(null, exerciciosPorGrupo[i][j], nomesGrupos[i].split(" e ")[0], 3, 12, 0.0, "", null));
                }
                grupo.setExercicios(exercicios);
                grupos.add(grupo);
            }
            return grupos;
        }
    /**
     * Sugere treinos comuns sem aparelhos para academias simples.
     */
    public static List<TreinoGrupoDTO> sugerirTreinosComuns(UUID alunoId) {
        // Exemplos de treinos comuns
        TreinoGrupoDTO treinoA = new TreinoGrupoDTO();
        treinoA.setAlunoId(alunoId);
        treinoA.setNome("Treino A - Peito e Tríceps");
        treinoA.setExercicios(List.of(
            new TreinoExercicioDTO(null, "Flexão de braço", "Peito", 3, 12, 0.0, "", null),
            new TreinoExercicioDTO(null, "Supino com halteres", "Peito", 3, 10, 0.0, "", null),
            new TreinoExercicioDTO(null, "Tríceps banco", "Tríceps", 3, 12, 0.0, "", null)
        ));

        TreinoGrupoDTO treinoB = new TreinoGrupoDTO();
        treinoB.setAlunoId(alunoId);
        treinoB.setNome("Treino B - Costas e Bíceps");
        treinoB.setExercicios(List.of(
            new TreinoExercicioDTO(null, "Remada unilateral", "Costas", 3, 10, 0.0, "", null),
            new TreinoExercicioDTO(null, "Pull-up (barra fixa)", "Costas", 3, 8, 0.0, "", null),
            new TreinoExercicioDTO(null, "Rosca direta", "Bíceps", 3, 12, 0.0, "", null)
        ));

        TreinoGrupoDTO treinoC = new TreinoGrupoDTO();
        treinoC.setAlunoId(alunoId);
        treinoC.setNome("Treino C - Ombro e Pernas");
        treinoC.setExercicios(List.of(
            new TreinoExercicioDTO(null, "Agachamento", "Pernas", 3, 15, 0.0, "", null),
            new TreinoExercicioDTO(null, "Avanço", "Pernas", 3, 12, 0.0, "", null),
            new TreinoExercicioDTO(null, "Elevação lateral", "Ombro", 3, 12, 0.0, "", null),
            new TreinoExercicioDTO(null, "Militar com halteres", "Ombro", 3, 10, 0.0, "", null)
        ));

        return List.of(treinoA, treinoB, treinoC);
    }

    private final TreinoGrupoRepository grupoRepo;
    private final AlunoRepository alunoRepo;
    private final ExercicioRepository exercicioRepo;

    public TreinoGrupoService(TreinoGrupoRepository grupoRepo, AlunoRepository alunoRepo,
                               ExercicioRepository exercicioRepo) {
        this.grupoRepo = grupoRepo;
        this.alunoRepo = alunoRepo;
        this.exercicioRepo = exercicioRepo;
    }

    public TreinoGrupoDTO criar(TreinoGrupoDTO dto) {
        UUID alunoId = dto.getAlunoId();
        Aluno aluno = alunoRepo.findById(alunoId)
            .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));

        TreinoGrupo grupo = new TreinoGrupo();
        grupo.setAluno(aluno);

        // Busca grupos musculares dos exercícios (caso já existam)
        List<String> gruposMuscularesList = dto.getExercicios() != null ?
            dto.getExercicios().stream()
                .map(TreinoExercicioDTO::getGrupoMuscular)
                .filter(g -> g != null && !g.isBlank())
                .distinct()
                .collect(Collectors.toList()) : List.of();

        String gruposMusculares = String.join(" e ", gruposMuscularesList);
        String nomeFinal = dto.getNome();
        if (!gruposMusculares.isBlank()) {
            nomeFinal = nomeFinal + " - " + gruposMusculares;
        }
        grupo.setNome(nomeFinal);

        TreinoGrupo salvo = grupoRepo.save(grupo);
        return new TreinoGrupoDTO(salvo.getId(), aluno.getId(), salvo.getNome());
    }

    public List<TreinoGrupoDTO> listarPorAluno(UUID alunoId) {
        return grupoRepo.findByAluno_IdAndAtivoTrue(alunoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void remover(UUID id) {
        TreinoGrupo grupo = grupoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado para remoção"));
        grupo.setAtivo(false);
        grupoRepo.save(grupo);
    }

    public TreinoGrupoDTO editar(UUID grupoId, TreinoGrupoDTO dto) {
        TreinoGrupo grupo = grupoRepo.findById(grupoId)
            .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        String nomeBase = dto.getNome() != null && !dto.getNome().isBlank() ? dto.getNome() : grupo.getNome();

        // Busca exercícios vinculados ao grupo
        List<Exercicio> exercicios = exercicioRepo.findByGrupo_Id(grupoId);
        String gruposMusculares = exercicios.stream()
            .map(Exercicio::getGrupoMuscular)
            .distinct()
            .collect(Collectors.joining(" e "));

        String nomeFinal = nomeBase;
        if (!gruposMusculares.isBlank()) {
            nomeFinal = nomeBase + " - " + gruposMusculares;
        }
        grupo.setNome(nomeFinal);

        TreinoGrupo salvo = grupoRepo.save(grupo);
        return toDTO(salvo);
    }

    private TreinoGrupoDTO toDTO(TreinoGrupo grupo) {
        return new TreinoGrupoDTO(
                grupo.getId(),
                grupo.getAluno().getId(),
                grupo.getNome()
        );
    }

    public void excluirGrupoComExercicios(UUID grupoId) {
        // 1. Buscar exercícios vinculados ao grupo
        List<Exercicio> exercicios = exercicioRepo.findByGrupo_Id(grupoId);
        for (Exercicio e : exercicios) {
            e.setAtivo(false);   // exclusão lógica
            e.setGrupo(null);    // quebra vínculo com grupo
            e.setObservacao(
                    (e.getObservacao() == null ? "" : e.getObservacao() + " | ")
                            + "Exercício desativado ao excluir grupo em " + LocalDateTime.now()
            );
        }
        exercicioRepo.saveAll(exercicios);

        // 2. Desativar o grupo (exclusão lógica)
        TreinoGrupo grupo = grupoRepo.findById(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));
        grupo.setAtivo(false);
        grupoRepo.save(grupo);
    }


}
