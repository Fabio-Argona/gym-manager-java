package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.AlunoDTO;
import com.treino_abc_backend.dto.AlunoHistoricoDTO;
import com.treino_abc_backend.dto.AlunoRegisterDTO;
import com.treino_abc_backend.dto.AlunoSenhaDTO;
import com.treino_abc_backend.dto.AlunoUpdateDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.repository.AlunoRepository;
import com.treino_abc_backend.service.AlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private AlunoRepository alunoRepository;

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        return ResponseEntity.ok(alunoService.listarTodos());
    }

    @PutMapping("/{id}/registro")
    public ResponseEntity<?> atualizar(@PathVariable UUID id, @RequestBody AlunoRegisterDTO dto) {
        try {
            return ResponseEntity.ok(alunoService.atualizar(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        try {
            AlunoDTO dto = alunoService.buscarPorId(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<?> buscarComHistorico(@PathVariable UUID id) {
        try {
            AlunoHistoricoDTO dto = alunoService.buscarComHistorico(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("erro", e.getMessage()));
        }
    }


    @PutMapping("/{id}/senha")
    public ResponseEntity<?> atualizarSenha(@PathVariable UUID id, @RequestBody AlunoSenhaDTO dto) {
        try {
            alunoService.atualizarSenha(id, dto.getSenha());
            return ResponseEntity.ok(Map.of("mensagem", "Senha atualizada com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}/login")
    public ResponseEntity<?> atualizarLogin(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        String novoLogin = body.get("login");
        if (novoLogin == null || novoLogin.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "O campo 'login' é obrigatório"));
        }
        try {
            alunoService.atualizarLogin(id, novoLogin);
            return ResponseEntity.ok(Map.of("mensagem", "Login atualizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}/fisico")
    public ResponseEntity<?> atualizarFisico(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        try {
            return ResponseEntity.ok(alunoService.atualizarFisico(id, body));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable UUID id) {
        try {
            String nome = alunoService.deletar(id);
            return ResponseEntity.ok(Map.of("mensagem", "Aluno '" + nome + "' deletado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aluno> atualizarAluno(
            @PathVariable UUID id,
            @RequestBody AlunoUpdateDTO alunoDTO
    ) {
        Optional<Aluno> optionalAluno = alunoRepository.findById(id);
        if (!optionalAluno.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Aluno aluno = optionalAluno.get();

        if (alunoDTO.getNome() != null) aluno.setNome(alunoDTO.getNome());
        if (alunoDTO.getTelefone() != null) aluno.setTelefone(alunoDTO.getTelefone());
        if (alunoDTO.getLogin() != null) aluno.setLogin(alunoDTO.getLogin());
        if (alunoDTO.getPassword() != null) aluno.setPassword(alunoDTO.getPassword());
        if (alunoDTO.getDataNascimento() != null) aluno.setDataNascimento(alunoDTO.getDataNascimento());

        alunoRepository.save(aluno);

        return ResponseEntity.ok(aluno);
    }

}
