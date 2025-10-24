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
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(AlunoRepository alunoRepository, EmailService emailService,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.alunoRepository = alunoRepository;
        this.emailService = emailService;
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

    public void enviarEmailRecuperacao(String email) {
        String normalizedEmail = email.trim().toLowerCase();

        Aluno aluno = alunoRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("Email não encontrado: " + normalizedEmail));

        String token = jwtUtil.generateToken(normalizedEmail, aluno.getCpf());
        String link = "https://seuapp.com/resetar-senha?token=" + token;

        emailService.enviar(normalizedEmail, "Recuperação de senha", "Clique aqui para redefinir: " + link);
    }

    public void redefinirSenha(String token, String novaSenha) {
        String email = jwtUtil.extractEmail(token).trim().toLowerCase();

        Aluno aluno = alunoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Token inválido ou email não encontrado: " + email));

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
