package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.AlunoDTO;
import com.treino_abc_backend.dto.TokenResponseDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.security.JwtUtil;
import com.treino_abc_backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Registro
    public Aluno register(Aluno aluno) {
        if (alunoRepository.existsByEmail(aluno.getEmail())) {
            throw new RuntimeException("Email já cadastrado: " + aluno.getEmail());
        }

        aluno.setPassword(passwordEncoder.encode(aluno.getPassword()));
        aluno.setRole("ROLE_USER");

        return alunoRepository.save(aluno);
    }

    // Login
    public TokenResponseDTO login(String email, String password) {
        System.out.println("[AUTH SERVICE] Iniciando login para: " + email);

        Aluno aluno = alunoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));

        if (!passwordEncoder.matches(password, aluno.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        String token = jwtUtil.generateToken(aluno.getEmail(), aluno.getCpf());

        AlunoDTO alunoDTO = new AlunoDTO();
        alunoDTO.setId(aluno.getId());
        alunoDTO.setNome(aluno.getNome());
        alunoDTO.setCpf(aluno.getCpf());
        alunoDTO.setEmail(aluno.getEmail());
        alunoDTO.setTelefone(aluno.getTelefone());
        alunoDTO.setDataNascimento(aluno.getDataNascimento());
        alunoDTO.setLogin(aluno.getLogin());

        System.out.println("[AUTH SERVICE] Login concluído com sucesso");

        return new TokenResponseDTO(token, alunoDTO);
    }

    public void enviarEmailRecuperacao(String email) {
        Aluno aluno = alunoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));

        String token = jwtUtil.generateToken(email, aluno.getCpf());

        String link = "https://seuapp.com/resetar-senha?token=" + token;

        emailService.enviar(email, "Recuperação de senha", "Clique aqui para redefinir: " + link);
    }

    public void redefinirSenha(String token, String novaSenha) {
        String email = jwtUtil.extractEmail(token);

        Aluno aluno = alunoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        aluno.setPassword(novaSenha);
        alunoRepository.save(aluno);
    }


}
