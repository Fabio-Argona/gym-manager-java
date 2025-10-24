package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.AlunoDTO;
import com.treino_abc_backend.dto.AlunoRegisterDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.repository.AlunoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Aluno findByEmail(String email) {
        return alunoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado com email: " + email));
    }

    public AlunoDTO buscarPorId(UUID id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));
        return toDTO(aluno);
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

    private AlunoDTO toDTO(Aluno aluno) {
        AlunoDTO dto = new AlunoDTO();
        dto.setId(aluno.getId());
        dto.setNome(aluno.getNome());
        dto.setCpf(aluno.getCpf());
        dto.setEmail(aluno.getEmail());
        dto.setTelefone(aluno.getTelefone());
        dto.setDataNascimento(aluno.getDataNascimento());
        dto.setLogin(aluno.getLogin());
        return dto;
    }
}
