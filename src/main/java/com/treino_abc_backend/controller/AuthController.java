package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.AlunoDTO;
import com.treino_abc_backend.dto.AlunoRegisterDTO;
import com.treino_abc_backend.dto.AlunoLoginDTO;
import com.treino_abc_backend.dto.RedefinirSenhaDTO;
import com.treino_abc_backend.dto.TokenResponseDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.security.JwtUtil;
import com.treino_abc_backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AlunoRegisterDTO dto) {
        try {
            Aluno aluno = new Aluno();
            aluno.setNome(dto.getNome());
            aluno.setCpf(dto.getCpf());
            aluno.setEmail(dto.getEmail());
            aluno.setTelefone(dto.getTelefone());
            aluno.setDataNascimento(dto.getDataNascimento());
            aluno.setLogin(dto.getLogin());
            aluno.setPassword(dto.getPassword());

            Aluno saved = authService.register(aluno);
            String token = jwtUtil.generateToken(saved.getEmail(), saved.getCpf());

            AlunoDTO alunoDTO = new AlunoDTO();
            alunoDTO.setId(saved.getId());
            alunoDTO.setNome(saved.getNome());
            alunoDTO.setCpf(saved.getCpf());
            alunoDTO.setEmail(saved.getEmail());
            alunoDTO.setTelefone(saved.getTelefone());
            alunoDTO.setDataNascimento(saved.getDataNascimento());
            alunoDTO.setLogin(saved.getLogin());

            return ResponseEntity.ok(new TokenResponseDTO(token, alunoDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AlunoLoginDTO dto) {
        try {
            TokenResponseDTO response = authService.login(dto.getEmail(), dto.getPassword());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/resetar-senha")
    public ResponseEntity<?> resetarSenha(@RequestBody RedefinirSenhaDTO dto) {
        try {
            authService.redefinirSenha(dto.getEmail(), dto.getCpf6(), dto.getNovaSenha());
            return ResponseEntity.ok("Senha redefinida com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
