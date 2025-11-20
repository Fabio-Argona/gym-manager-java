package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.EvolucaoMedidasDTO;
import com.treino_abc_backend.entity.EvolucaoMedidas;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.repository.EvolucaoMedidasRepository;
import com.treino_abc_backend.repository.AlunoRepository;
import org.springframework.stereotype.Service;

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
        evolucao.setData(dto.getData());
        evolucao.setPeso(dto.getPeso());
        evolucao.setPercentualGordura(dto.getPercentualGordura());
        evolucao.setPercentualMusculo(dto.getPercentualMusculo());
        evolucao.setImc(dto.getImc());
        evolucao.setCintura(dto.getCintura());
        evolucao.setAbdomen(dto.getAbdomen());
        evolucao.setQuadril(dto.getQuadril());
        evolucao.setPeito(dto.getPeito());
        evolucao.setBracoDireito(dto.getBracoDireito());
        evolucao.setBracoEsquerdo(dto.getBracoEsquerdo());
        evolucao.setCoxaDireita(dto.getCoxaDireita());
        evolucao.setCoxaEsquerda(dto.getCoxaEsquerda());

        return evolucaoRepository.save(evolucao);
    }

    public EvolucaoMedidas atualizar(UUID id, EvolucaoMedidasDTO dto) {
        EvolucaoMedidas evolucao = evolucaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evolução não encontrada"));

        evolucao.setData(dto.getData());
        evolucao.setPeso(dto.getPeso());
        evolucao.setPercentualGordura(dto.getPercentualGordura());
        evolucao.setPercentualMusculo(dto.getPercentualMusculo());
        evolucao.setImc(dto.getImc());
        evolucao.setCintura(dto.getCintura());
        evolucao.setAbdomen(dto.getAbdomen());
        evolucao.setQuadril(dto.getQuadril());
        evolucao.setPeito(dto.getPeito());
        evolucao.setBracoDireito(dto.getBracoDireito());
        evolucao.setBracoEsquerdo(dto.getBracoEsquerdo());
        evolucao.setCoxaDireita(dto.getCoxaDireita());
        evolucao.setCoxaEsquerda(dto.getCoxaEsquerda());

        return evolucaoRepository.save(evolucao);
    }

    public List<EvolucaoMedidas> listarPorAluno(UUID alunoId) {
        if (!alunoRepository.existsById(alunoId)) {
            throw new RuntimeException("Aluno não encontrado");
        }
        return evolucaoRepository.findByAlunoIdOrderByDataAsc(alunoId);
    }

    public void deletar(UUID id) {
        EvolucaoMedidas evolucao = evolucaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evolução não encontrada"));
        evolucaoRepository.delete(evolucao);
    }
}
