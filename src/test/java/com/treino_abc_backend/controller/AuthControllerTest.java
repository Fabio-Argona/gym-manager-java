package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.*;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.security.JwtUtil;
import com.treino_abc_backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Mock
    private AuthService authService;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_deveRetornarTokenResponseDTO() {
        AlunoRegisterDTO dto = new AlunoRegisterDTO();
        dto.setNome("João");
        dto.setCpf("12345678900");
        dto.setEmail("joao@email.com");
        dto.setTelefone("999999999");
        dto.setDataNascimento(java.time.LocalDate.parse("2000-01-01"));
        dto.setLogin("joao");
        dto.setPassword("senha");

        Aluno aluno = new Aluno();
        aluno.setId(java.util.UUID.randomUUID());
        aluno.setNome(dto.getNome());
        aluno.setCpf(dto.getCpf());
        aluno.setEmail(dto.getEmail());
        aluno.setTelefone(dto.getTelefone());
        aluno.setDataNascimento(dto.getDataNascimento());
        aluno.setLogin(dto.getLogin());
        aluno.setPassword(dto.getPassword());

        when(authService.register(any(Aluno.class))).thenReturn(aluno);
        when(jwtUtil.generateToken(aluno.getEmail(), aluno.getCpf())).thenReturn("token123");

        ResponseEntity<?> response = authController.register(dto);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof TokenResponseDTO);
        TokenResponseDTO tokenResponse = (TokenResponseDTO) response.getBody();
        assertEquals("token123", tokenResponse.getToken());
        assertEquals(aluno.getNome(), tokenResponse.getAluno().getNome());
    }

    @Test
    void register_deveRetornarBadRequest() {
        AlunoRegisterDTO dto = new AlunoRegisterDTO();
        when(authService.register(any(Aluno.class))).thenThrow(new RuntimeException("Erro registro"));
        ResponseEntity<?> response = authController.register(dto);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Erro registro"));
    }

    @Test
    void login_deveRetornarTokenResponseDTO() {
        AlunoLoginDTO dto = new AlunoLoginDTO();
        dto.setEmail("joao@email.com");
        dto.setPassword("senha");
        TokenResponseDTO tokenResponse = new TokenResponseDTO("token123", new AlunoDTO());
        when(authService.login(dto.getEmail(), dto.getPassword())).thenReturn(tokenResponse);
        ResponseEntity<?> response = authController.login(dto);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(tokenResponse, response.getBody());
    }

    @Test
    void login_deveRetornarErro401() {
        AlunoLoginDTO dto = new AlunoLoginDTO();
        dto.setEmail("joao@email.com");
        dto.setPassword("senha");
        when(authService.login(eq(dto.getEmail()), eq(dto.getPassword()))).thenThrow(new RuntimeException("Erro login"));
        ResponseEntity<?> response = authController.login(dto);
        assertEquals(401, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Erro login"));
    }

    @Test
    void resetarSenha_deveRetornarSucesso() {
        RedefinirSenhaDTO dto = new RedefinirSenhaDTO();
        dto.setEmail("joao@email.com");
        dto.setCpf6("123456");
        dto.setNovaSenha("novaSenha");
        doNothing().when(authService).redefinirSenha(dto.getEmail(), dto.getCpf6(), dto.getNovaSenha());
        ResponseEntity<?> response = authController.resetarSenha(dto);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Senha redefinida com sucesso", response.getBody());
    }

    @Test
    void resetarSenha_deveRetornarBadRequest() {
        RedefinirSenhaDTO dto = new RedefinirSenhaDTO();
        dto.setEmail("joao@email.com");
        dto.setCpf6("123456");
        dto.setNovaSenha("novaSenha");
        doThrow(new RuntimeException("Erro redefinir senha")).when(authService).redefinirSenha(eq(dto.getEmail()), eq(dto.getCpf6()), eq(dto.getNovaSenha()));
        ResponseEntity<?> response = authController.resetarSenha(dto);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Erro redefinir senha"));
    }
}
