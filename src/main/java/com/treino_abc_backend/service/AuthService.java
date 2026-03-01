package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.AlunoDTO;
import com.treino_abc_backend.dto.TokenResponseDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(AlunoRepository alunoRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Aluno register(Aluno aluno) {
        String normalizedEmail = aluno.getEmail().trim().toLowerCase();

        if (alunoRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email já cadastrado: " + normalizedEmail);
        }

        aluno.setEmail(normalizedEmail);
        aluno.setPassword(passwordEncoder.encode(aluno.getPassword()));
        aluno.setRole("ROLE_USER");

        return alunoRepository.save(aluno);
    }

    public TokenResponseDTO login(String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();

        Aluno aluno = alunoRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("Email não encontrado: " + normalizedEmail));

        if (!passwordEncoder.matches(password, aluno.getPassword())) {
            throw new RuntimeException("Senha inválida para o email: " + normalizedEmail);
        }

        String token = jwtUtil.generateToken(aluno.getEmail(), aluno.getCpf());
        return new TokenResponseDTO(token, toDTO(aluno));
    }

    public void redefinirSenha(String email, String cpf6, String novaSenha) {
        String normalizedEmail = email.trim().toLowerCase();

        Aluno aluno = alunoRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("Email não encontrado."));

        // Remove formatação do CPF e compara os primeiros 6 dígitos
        String cpfDigitos = aluno.getCpf().replaceAll("[^0-9]", "");
        String cpf6Limpo = cpf6.replaceAll("[^0-9]", "");

        if (!cpfDigitos.startsWith(cpf6Limpo)) {
            throw new RuntimeException("CPF incorreto.");
        }

        aluno.setPassword(passwordEncoder.encode(novaSenha));
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
        return dto;
    }
}
