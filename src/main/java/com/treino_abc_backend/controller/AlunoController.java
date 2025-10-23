package com.treino_abc_backend.controller;

import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.security.JwtUtil;
import com.treino_abc_backend.service.AlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Listar todos
    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(alunoService.listarTodos());
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(alunoService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Deletar
    public String deletar(UUID id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado para deletar."));
        String nome = aluno.getNome();
        alunoRepository.deleteById(id);
        return nome;
    }




}
