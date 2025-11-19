package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.AlunoDTO;
import com.treino_abc_backend.dto.AlunoRegisterDTO;
import com.treino_abc_backend.dto.AlunoSenhaDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.security.JwtUtil;
import com.treino_abc_backend.service.AlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        return ResponseEntity.ok(alunoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        return alunoRepository.findById(id)
                .map(aluno -> {
                    AlunoDTO dto = new AlunoDTO(
                            aluno.getId(),
                            aluno.getNome(),
                            aluno.getCpf(),
                            aluno.getEmail(),
                            aluno.getTelefone(),
                            aluno.getDataNascimento(),
                            aluno.getLogin()
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.status(404).body((AlunoDTO) Map.of("erro", "Aluno não encontrado")));
    }

    @PutMapping("/{id}/nome")
    public ResponseEntity<?> atualizarNome(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        String novoNome = body.get("nome");

        return alunoRepository.findById(id)
                .map(aluno -> {
                    aluno.setNome(novoNome);
                    alunoRepository.save(aluno);
                    return ResponseEntity.ok(Map.of("mensagem", "Nome atualizado com sucesso"));
                })
                .orElse(ResponseEntity.status(404).body(Map.of("erro", "Aluno não encontrado")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable UUID id) {
        return alunoRepository.findById(id)
                .map(aluno -> {
                    alunoRepository.deleteById(id);
                    return ResponseEntity.ok(Map.of("mensagem", "Aluno '" + aluno.getNome() + "' deletado com sucesso"));
                })
                .orElse(ResponseEntity.status(404).body(Map.of("erro", "Aluno não encontrado")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable UUID id, @RequestBody AlunoRegisterDTO dto) {
        try {
            return ResponseEntity.ok(alunoService.atualizar(id, dto));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}/senha")
    public ResponseEntity<?> atualizarSenha(@PathVariable UUID id, @RequestBody AlunoSenhaDTO dto) {
        try {
            alunoService.atualizarSenha(id, dto.getSenha());
            return ResponseEntity.ok(Map.of("mensagem", "Senha atualizada com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}/login")
    public ResponseEntity<?> atualizarLogin(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        try {
            String novoLogin = body.get("login");
            alunoService.atualizarLogin(id, novoLogin);
            return ResponseEntity.ok(Map.of("mensagem", "Login atualizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("erro", e.getMessage()));
        }
    }







}
