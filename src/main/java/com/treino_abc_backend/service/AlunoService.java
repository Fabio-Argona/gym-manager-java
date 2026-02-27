package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.AlunoDTO;
import com.treino_abc_backend.dto.AlunoHistoricoDTO;
import com.treino_abc_backend.dto.AlunoRegisterDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.repository.AlunoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    public AlunoService(AlunoRepository alunoRepository, PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AlunoDTO salvar(AlunoRegisterDTO dto) {
        if (alunoRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado: " + dto.getEmail());
        }

        Aluno aluno = new Aluno();
        aluno.setNome(dto.getNome());
        aluno.setCpf(dto.getCpf());
        aluno.setEmail(dto.getEmail());
        aluno.setTelefone(dto.getTelefone());
        aluno.setDataNascimento(dto.getDataNascimento());
        aluno.setLogin(dto.getLogin());
        aluno.setPassword(passwordEncoder.encode(dto.getPassword()));
        aluno.setRole("ROLE_USER");

        Aluno saved = alunoRepository.save(aluno);
        return toDTO(saved);
    }

    public AlunoDTO buscarPorId(UUID id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));
        return toDTO(aluno);
    }

    public AlunoHistoricoDTO buscarComHistorico(UUID id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

        AlunoHistoricoDTO dto = new AlunoHistoricoDTO();
        dto.setId(aluno.getId());
        dto.setNome(aluno.getNome());
        dto.setCpf(aluno.getCpf());
        dto.setEmail(aluno.getEmail());
        dto.setTelefone(aluno.getTelefone());
        dto.setDataNascimento(aluno.getDataNascimento());
        dto.setLogin(aluno.getLogin());
        dto.setEvolucoes(aluno.getEvolucoes());

        return dto;
    }

    public List<AlunoDTO> listarTodos() {
        return alunoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public String deletar(UUID id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado para deletar."));
        String nome = aluno.getNome();
        alunoRepository.deleteById(id);
        return nome;
    }

    public AlunoDTO atualizar(UUID id, AlunoRegisterDTO dto) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        aluno.setNome(dto.getNome());
        aluno.setCpf(dto.getCpf());
        aluno.setEmail(dto.getEmail());
        aluno.setTelefone(dto.getTelefone());
        aluno.setDataNascimento(dto.getDataNascimento());
        aluno.setLogin(dto.getLogin());

        alunoRepository.save(aluno);

        return toDTO(aluno);
    }

    public void atualizarSenha(UUID id, String novaSenha) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        aluno.setPassword(passwordEncoder.encode(novaSenha));
        alunoRepository.save(aluno);
    }

    public void atualizarLogin(UUID id, String novoLogin) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        aluno.setLogin(novoLogin);
        alunoRepository.save(aluno);
    }

    private AlunoDTO toDTO(Aluno aluno) {
        AlunoDTO dto = new AlunoDTO();
        dto.setId(aluno.getId());
        dto.setNome(aluno.getNome());
        dto.setCpf(aluno.getCpf());
        dto.setEmail(aluno.getEmail());
        dto.setTelefone(aluno.getTelefone());
        dto.setDataNascimento(aluno.getDataNascimento());
        dto.setLogin(aluno.getLogin());
        dto.setSexo(aluno.getSexo());
        dto.setPesoAtual(aluno.getPesoAtual());
        dto.setAltura(aluno.getAltura());
        dto.setPercentualGordura(aluno.getPercentualGordura());
        dto.setPercentualMusculo(aluno.getPercentualMusculo());
        dto.setImc(aluno.getImc());
        dto.setObjetivo(aluno.getObjetivo());
        dto.setNivelTreinamento(aluno.getNivelTreinamento());
        return dto;
    }

    public AlunoDTO atualizarFisico(UUID id, Map<String, Object> body) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        if (body.containsKey("sexo")) aluno.setSexo((String) body.get("sexo"));
        if (body.containsKey("pesoAtual")) aluno.setPesoAtual(toDouble(body.get("pesoAtual")));
        if (body.containsKey("altura")) aluno.setAltura(toDouble(body.get("altura")));
        if (body.containsKey("percentualGordura")) aluno.setPercentualGordura(toDouble(body.get("percentualGordura")));
        if (body.containsKey("percentualMusculo")) aluno.setPercentualMusculo(toDouble(body.get("percentualMusculo")));

        // IMC calculado pelo backend: peso / altura²
        Double peso = aluno.getPesoAtual();
        Double alt = aluno.getAltura();
        if (peso != null && alt != null && alt > 0) {
            double imc = peso / (alt * alt);
            aluno.setImc(Math.round(imc * 100.0) / 100.0);
        }

        alunoRepository.save(aluno);
        return toDTO(aluno);
    }

    private Double toDouble(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).doubleValue();
        try { return Double.parseDouble(val.toString()); } catch (Exception e) { return null; }
    }
}
