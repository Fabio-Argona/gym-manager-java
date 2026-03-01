package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.EvolucaoMedidasDTO;
import com.treino_abc_backend.entity.EvolucaoMedidas;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.repository.EvolucaoMedidasRepository;
import com.treino_abc_backend.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class EvolucaoMedidasService {

    private final EvolucaoMedidasRepository evolucaoRepository;
    private final AlunoRepository alunoRepository;

    public EvolucaoMedidasService(EvolucaoMedidasRepository evolucaoRepository, AlunoRepository alunoRepository) {
        this.evolucaoRepository = evolucaoRepository;
        this.alunoRepository = alunoRepository;
    }

    public EvolucaoMedidas salvar(EvolucaoMedidasDTO dto) {
        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        EvolucaoMedidas evolucao = new EvolucaoMedidas();
        evolucao.setAluno(aluno);
        evolucao.setData(dto.getData() != null ? dto.getData() : LocalDate.now());
        evolucao.setPeso(dto.getPeso());
        evolucao.setAltura(dto.getAltura());
        evolucao.setPercentualGordura(dto.getPercentualGordura());
        evolucao.setPercentualMusculo(dto.getPercentualMusculo());
        evolucao.setCintura(dto.getCintura());
        evolucao.setAbdomen(dto.getAbdomen());
        evolucao.setQuadril(dto.getQuadril());
        evolucao.setPeito(dto.getPeito());
        evolucao.setBracoDireito(dto.getBracoDireito());
        evolucao.setBracoEsquerdo(dto.getBracoEsquerdo());
        evolucao.setCoxaDireita(dto.getCoxaDireita());
        evolucao.setCoxaEsquerda(dto.getCoxaEsquerda());
        // Calcula IMC automaticamente: peso / (altura_metros)²
        evolucao.setImc(calcularImc(dto.getPeso(), dto.getAltura()));

        // Atualiza também os campos da tabela aluno para exibição no perfil
        if (dto.getPeso() != null) aluno.setPesoAtual(dto.getPeso());
        if (dto.getAltura() != null) aluno.setAltura(dto.getAltura());
        if (dto.getPercentualGordura() != null) aluno.setPercentualGordura(dto.getPercentualGordura());
        if (dto.getPercentualMusculo() != null) aluno.setPercentualMusculo(dto.getPercentualMusculo());
        aluno.setImc(evolucao.getImc());
        alunoRepository.save(aluno);

        return evolucaoRepository.save(evolucao);
    }

    public EvolucaoMedidas atualizar(UUID id, EvolucaoMedidasDTO dto) {
        EvolucaoMedidas evolucao = evolucaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evolução não encontrada"));

        if (dto.getData() != null) evolucao.setData(dto.getData());
        evolucao.setPeso(dto.getPeso());
        evolucao.setAltura(dto.getAltura());
        evolucao.setPercentualGordura(dto.getPercentualGordura());
        evolucao.setPercentualMusculo(dto.getPercentualMusculo());
        evolucao.setImc(calcularImc(dto.getPeso(), dto.getAltura()));
        evolucao.setCintura(dto.getCintura());
        evolucao.setAbdomen(dto.getAbdomen());
        evolucao.setQuadril(dto.getQuadril());
        evolucao.setPeito(dto.getPeito());
        evolucao.setBracoDireito(dto.getBracoDireito());
        evolucao.setBracoEsquerdo(dto.getBracoEsquerdo());
        evolucao.setCoxaDireita(dto.getCoxaDireita());
        evolucao.setCoxaEsquerda(dto.getCoxaEsquerda());

        // Espelha no aluno (busca do repositório para evitar LazyInitializationException)
        try {
            UUID alunoId = evolucao.getAluno().getId();
            alunoRepository.findById(alunoId).ifPresent(aluno -> {
                if (dto.getPeso() != null) aluno.setPesoAtual(dto.getPeso());
                if (dto.getAltura() != null) aluno.setAltura(dto.getAltura());
                if (dto.getPercentualGordura() != null) aluno.setPercentualGordura(dto.getPercentualGordura());
                if (dto.getPercentualMusculo() != null) aluno.setPercentualMusculo(dto.getPercentualMusculo());
                aluno.setImc(evolucao.getImc());
                alunoRepository.save(aluno);
            });
        } catch (Exception ignored) {}

        return evolucaoRepository.save(evolucao);
    }

    public List<EvolucaoMedidas> listarPorAluno(UUID alunoId) {
        if (!alunoRepository.existsById(alunoId)) {
            throw new RuntimeException("Aluno não encontrado");
        }
        return evolucaoRepository.findByAlunoIdOrderByDataAsc(alunoId);
    }

    private Double calcularImc(Double peso, Double alturaParam) {
        if (peso == null || alturaParam == null || alturaParam == 0) return null;
        double altMetros = alturaParam > 3 ? alturaParam / 100.0 : alturaParam;
        double imc = peso / (altMetros * altMetros);
        return Math.round(imc * 100.0) / 100.0;
    }
}
