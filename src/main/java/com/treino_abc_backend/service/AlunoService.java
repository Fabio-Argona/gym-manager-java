package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.AlunoDTO;
import com.treino_abc_backend.dto.AlunoUpdateDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.repository.AlunoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    public AlunoService(AlunoRepository alunoRepository, PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AlunoDTO buscarPorId(UUID id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));
        return toDTO(aluno);
    }

    public AlunoDTO atualizarCompleto(UUID id, AlunoUpdateDTO dto) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        if (dto.getNome() != null) aluno.setNome(dto.getNome());
        if (dto.getTelefone() != null) aluno.setTelefone(dto.getTelefone());
        if (dto.getLogin() != null) aluno.setLogin(dto.getLogin());
        if (dto.getPassword() != null) aluno.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getDataNascimento() != null) aluno.setDataNascimento(dto.getDataNascimento());

        return toDTO(alunoRepository.save(aluno));
    }

    public AlunoDTO toDTO(Aluno aluno) {
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

        // /fisico agora salva apenas dados estáticos: sexo e altura
        if (body.containsKey("sexo")) aluno.setSexo((String) body.get("sexo"));
        if (body.containsKey("altura")) aluno.setAltura(toDouble(body.get("altura")));

        alunoRepository.save(aluno);
        return toDTO(aluno);
    }

    public AlunoDTO atualizarObjetivo(UUID id, String objetivo, String nivelTreinamento) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        if (objetivo != null) aluno.setObjetivo(objetivo);
        if (nivelTreinamento != null) aluno.setNivelTreinamento(nivelTreinamento);

        alunoRepository.save(aluno);
        return toDTO(aluno);
    }

    private Double toDouble(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).doubleValue();
        try { return Double.parseDouble(val.toString()); } catch (Exception e) { return null; }
    }
}
